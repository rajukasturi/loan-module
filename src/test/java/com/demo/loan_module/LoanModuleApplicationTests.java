package com.demo.loan_module;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic integration tests for the Loan Module Application.
 */
@SpringBootTest
@ActiveProfiles("test")
class LoanModuleApplicationTests {

    @Test
    void contextLoads() {
        // Basic context loading test
        assertTrue(true, "Context loaded successfully");
    }
}
