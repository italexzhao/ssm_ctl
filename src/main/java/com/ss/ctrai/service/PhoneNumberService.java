package com.ss.ctrai.service;

import com.ss.ctrai.dto.AccountSegment;
import com.ss.ctrai.entity.PhoneNumber;
import com.ss.ctrai.enums.AccountType;
import com.ss.ctrai.mapper.PhoneNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PhoneNumberService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 200L;

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE,
            timeout = 10,
            rollbackFor = Exception.class)
    public AccountSegment getNextAvailableNumber() {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRIES) {
            try {
                // 首先尝试获取未使用的号码
                PhoneNumber account = phoneNumberMapper.findNextAvailableNumber();

                // 如果没有未使用的号码，则获取最早使用的号码
                if (account == null) {
                    account = phoneNumberMapper.findLeastUsedNumber();
                }

                if (account == null) {
                    log.warn("No available numbers found");
                    throw new RuntimeException("No available numbers");
                }

                account.setLastUsedTime(LocalDateTime.now());
                if (phoneNumberMapper.updateLastUsedTime(account) == 0) {
                    log.warn("Optimistic lock failed for number: {}, retrying...",
                            account.getPhoneNumber());
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        Thread.sleep(RETRY_DELAY);
                        continue;
                    }
                    throw new RuntimeException("Failed to update number after retries");
                }

                log.debug("Successfully allocated number: {}", account.getPhoneNumber());
                return createAccountSegment(account);

            } catch (Exception e) {
                lastException = e;
                log.warn("Failed to get number, attempt {}/{}: {}",
                        retryCount + 1, MAX_RETRIES, e.getMessage());
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted", ie);
                    }
                    continue;
                }
                break;
            }
        }

        throw new RuntimeException("Failed to get number after " + MAX_RETRIES +
                " attempts", lastException);
    }

    private AccountSegment createAccountSegment(PhoneNumber account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        AccountSegment result = new AccountSegment();
        result.setType(account.getType());
        result.setWechatNumber(account.getWechatNumber());
        result.setNumList(new ArrayList<>());

        if (account.getType() == AccountType.PHONE) {
            String number = account.getPhoneNumber();
            if (number == null || number.length() < 7) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            // 分割手机号：前3位 + 中间4位 + 后4位
            String prefix = number.substring(0, 5);
            String suffix = number.substring(5);

            result.setPrefix(prefix);
            result.setSuffix(suffix);

            // 只添加前缀和后缀
            result.getNumList().add(prefix);
            result.getNumList().add(suffix);

        } else {
            result.setPrefix(account.getPhoneNumber());
            result.getNumList().add(account.getPhoneNumber());
        }

        return result;
    }

    public List<PhoneNumber> getAllPhoneNumbers() {
        return phoneNumberMapper.findAll();
    }

    @Transactional
    public void setPhoneNumberStatus(Long id, boolean status) {
        PhoneNumber phoneNumber = phoneNumberMapper.findById(id);
        if (phoneNumber == null) {
            throw new RuntimeException("Phone number not found");
        }

        phoneNumber.setStatus(status);
        if (phoneNumberMapper.updateStatus(phoneNumber) == 0) {
            throw new RuntimeException("Failed to update status, please retry");
        }
    }

    @Transactional
    public PhoneNumber addPhoneNumber(String number, AccountType type, String wechatNumber) {
        if (phoneNumberMapper.existsByPhoneNumber(number) > 0) {
            throw new RuntimeException("Number already exists");
        }

        PhoneNumber newAccount = new PhoneNumber();
        newAccount.setPhoneNumber(number);
        newAccount.setType(type);
        newAccount.setWechatNumber(wechatNumber);
        newAccount.setStatus(true);

        phoneNumberMapper.insert(newAccount);
        return newAccount;
    }

    @Transactional
    public void deletePhoneNumber(Long id) {
        if (phoneNumberMapper.deleteById(id) == 0) {
            throw new RuntimeException("Phone number not found");
        }
    }

    @Transactional
    public void updateWechatNumber(Long id, String wechatNumber) {
        PhoneNumber account = phoneNumberMapper.findById(id);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        account.setWechatNumber(wechatNumber);
        if (phoneNumberMapper.updateWechatNumber(account) == 0) {
            throw new RuntimeException("Failed to update wechat number, please retry");
        }
    }
} 