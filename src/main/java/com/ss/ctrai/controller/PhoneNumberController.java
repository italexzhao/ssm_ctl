package com.ss.ctrai.controller;

import com.ss.ctrai.dto.AccountSegment;
import com.ss.ctrai.dto.ResponseData;
import com.ss.ctrai.entity.PhoneNumber;
import com.ss.ctrai.enums.AccountType;
import com.ss.ctrai.service.PhoneNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phone")
@Slf4j
public class PhoneNumberController {

    @Autowired
    private PhoneNumberService phoneNumberService;

    @GetMapping("/next")
    public ResponseData<AccountSegment> getNextNumber() {
        try {
            AccountSegment result = phoneNumberService.getNextAvailableNumber();
            return ResponseData.success(result);
        } catch (Exception e) {
            log.error("Failed to get next number", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseData<List<PhoneNumber>> getAllNumbers() {
        try {
            List<PhoneNumber> numbers = phoneNumberService.getAllPhoneNumbers();
            return ResponseData.success(numbers);
        } catch (Exception e) {
            log.error("Failed to get all numbers", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseData<Void> updateStatus(
            @PathVariable Long id, 
            @RequestParam boolean active) {
        try {
            phoneNumberService.setPhoneNumberStatus(id, active);
            return ResponseData.success(null);
        } catch (Exception e) {
            log.error("Failed to update status", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }

    @PostMapping
    public ResponseData<PhoneNumber> addNumber(
            @RequestParam String number,
            @RequestParam(defaultValue = "PHONE") AccountType type,
            @RequestParam(required = false) String wechatNumber) {
        try {
            PhoneNumber result = phoneNumberService.addPhoneNumber(number, type, wechatNumber);
            return ResponseData.success(result);
        } catch (Exception e) {
            log.error("Failed to add number", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }

    @PutMapping("/{id}/wechat")
    public ResponseData<Void> updateWechatNumber(
            @PathVariable Long id,
            @RequestParam String wechatNumber) {
        try {
            phoneNumberService.updateWechatNumber(id, wechatNumber);
            return ResponseData.success(null);
        } catch (Exception e) {
            log.error("Failed to update wechat number", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteNumber(@PathVariable Long id) {
        try {
            phoneNumberService.deletePhoneNumber(id);
            return ResponseData.success(null);
        } catch (Exception e) {
            log.error("Failed to delete number", e);
            return ResponseData.fail(ResponseData.FAIL_500, e.getMessage());
        }
    }
} 