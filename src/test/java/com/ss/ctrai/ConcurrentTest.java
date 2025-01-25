package com.ss.ctrai;

import com.ss.ctrai.dto.AccountSegment;
import com.ss.ctrai.dto.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"/schema.sql", "/data.sql"}, 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
     config = @SqlConfig(encoding = "UTF-8", 
                        separator = ";", 
                        commentPrefix = "--",
                        errorMode = SqlConfig.ErrorMode.FAIL_ON_ERROR,
                        transactionMode = SqlConfig.TransactionMode.ISOLATED))
class ConcurrentTest {

    @Autowired
    private DataSource dataSource;

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    
    @BeforeEach
    void setUp() throws Exception {
        // 验证数据库连接
        try (Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
            }
        } catch (Exception e) {
            log.error("Database connection failed", e);
            throw e;
        }
    }

    @Test
    void testConcurrentGetNumber() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        // 所有线程同时开始
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    startLatch.await(); // 等待统一开始
                    String url = String.format("http://localhost:%d/api/phone/next", port);
                    AccountSegment result = getNumberWithRetry(url, threadIndex);
                    
                    if (result != null && result.getNumList() != null && result.getNumList().size() >= 2) {
                        // 拼接前缀和后缀
                        String number = result.getNumList().get(0) + result.getNumList().get(1);
                        results.add(number);
                        log.debug("Thread {} got number: {}", threadIndex, number);
                    } else {
                        throw new RuntimeException("Invalid result: " + result);
                    }
                } catch (Exception e) {
                    log.error("Thread {} failed: {}", threadIndex, e.getMessage());
                    errors.add(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown(); // 开始所有线程
        boolean completed = endLatch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();
        
        if (!completed) {
            throw new AssertionError("Test timed out!");
        }
        
        assertThat(errors).isEmpty();
        assertThat(results).hasSize(threadCount);
        assertThat(findDuplicates(results)).isEmpty();
    }
    
    private AccountSegment getNumberWithRetry(String url, int threadIndex) throws InterruptedException {
        int maxRetries = 3;
        AccountSegment result = null;
        
        for (int retry = 0; retry < maxRetries && result == null; retry++) {
            try {
                ResponseData<AccountSegment> response = restTemplate.getForObject(
                    url, 
                    new ParameterizedTypeReference<ResponseData<AccountSegment>>() {}
                );
                
                if (response == null || response.getCode() != ResponseData.SUCCESS) {
                    log.warn("Thread {} got error response on retry {}: {}", 
                            threadIndex, retry, response);
                    if (retry == maxRetries - 1) {
                        throw new RuntimeException("Invalid response: " + response);
                    }
                    Thread.sleep(100);
                    continue;
                }
                
                result = response.getData();
                if (result == null || result.getSegments() == null) {
                    log.warn("Thread {} got invalid result on retry {}: {}", 
                            threadIndex, retry, result);
                    if (retry == maxRetries - 1) {
                        throw new RuntimeException("Invalid result: " + result);
                    }
                    Thread.sleep(100);
                    continue;
                }
            } catch (Exception e) {
                log.warn("Thread {} retry {}: {}", threadIndex, retry, e.getMessage());
                if (retry == maxRetries - 1) {
                    throw e;
                }
                Thread.sleep(100);
            }
        }
        
        return result;
    }
    
    private List<String> findDuplicates(List<String> results) {
        List<String> duplicates = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        
        for (String result : results) {
            if (seen.contains(result)) {
                duplicates.add(result);
            } else {
                seen.add(result);
            }
        }
        
        return duplicates;
    }
} 