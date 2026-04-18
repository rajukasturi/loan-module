package com.demo.loan_module.model;

import lombok.Data;

/**
 * Legacy request model for loan application.
 * Used for validation compatibility.
 * For new development, use CreateLoanApplicationRequest in the dto.request package.
 */
@Data
public class LoanRequest {
    private String fullName;
    private String panNumber;
    private Double monthlyIncome;
    private Double requestedAmount;
}
