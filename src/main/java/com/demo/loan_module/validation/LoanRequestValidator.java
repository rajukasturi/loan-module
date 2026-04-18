package com.demo.loan_module.validation;

import com.demo.loan_module.exception.InvalidLoanRequestException;
import com.demo.loan_module.model.LoanRequest;
import org.springframework.stereotype.Component;

/**
 * Validator for loan requests.
 * Ensures all required fields are present and meet business rules.
 */
@Component
public class LoanRequestValidator {
    
    // Minimum acceptable values for validation
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_PAN_LENGTH = 10;
    private static final int MAX_PAN_LENGTH = 10;
    private static final double MIN_MONTHLY_INCOME = 10000.0;
    private static final double MIN_REQUESTED_AMOUNT = 1000.0;
    private static final double MAX_REQUESTED_AMOUNT = 10000000.0; // 1 crore
    
    /**
     * Validates a loan request.
     * @param request The loan request to validate
     * @throws InvalidLoanRequestException if validation fails
     */
    public void validate(LoanRequest request) {
        if (request == null) {
            throw new InvalidLoanRequestException("Loan request cannot be null");
        }
        
        validateFullName(request.getFullName());
        validatePanNumber(request.getPanNumber());
        validateMonthlyIncome(request.getMonthlyIncome());
        validateRequestedAmount(request.getRequestedAmount());
    }
    
    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidLoanRequestException("Full name is required");
        }
        if (fullName.trim().length() < MIN_NAME_LENGTH) {
            throw new InvalidLoanRequestException(
                    "Full name must be at least %d characters", MIN_NAME_LENGTH);
        }
        if (fullName.trim().length() > MAX_NAME_LENGTH) {
            throw new InvalidLoanRequestException(
                    "Full name must not exceed %d characters", MAX_NAME_LENGTH);
        }
    }
    
    private void validatePanNumber(String panNumber) {
        if (panNumber == null || panNumber.trim().isEmpty()) {
            throw new InvalidLoanRequestException("PAN number is required");
        }
        String trimmedPan = panNumber.trim().toUpperCase();
        if (trimmedPan.length() != MIN_PAN_LENGTH) {
            throw new InvalidLoanRequestException(
                    "PAN number must be exactly %d characters", MIN_PAN_LENGTH);
        }
        if (!trimmedPan.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {
            throw new InvalidLoanRequestException(
                    "PAN number must be in valid format (e.g., ABCDE1234F)");
        }
    }
    
    private void validateMonthlyIncome(Double monthlyIncome) {
        if (monthlyIncome == null) {
            throw new InvalidLoanRequestException("Monthly income is required");
        }
        if (monthlyIncome < MIN_MONTHLY_INCOME) {
            throw new InvalidLoanRequestException(
                    "Monthly income must be at least ₹%.2f", MIN_MONTHLY_INCOME);
        }
    }
    
    private void validateRequestedAmount(Double requestedAmount) {
        if (requestedAmount == null) {
            throw new InvalidLoanRequestException("Requested amount is required");
        }
        if (requestedAmount < MIN_REQUESTED_AMOUNT) {
            throw new InvalidLoanRequestException(
                    "Requested amount must be at least ₹%.2f", MIN_REQUESTED_AMOUNT);
        }
        if (requestedAmount > MAX_REQUESTED_AMOUNT) {
            throw new InvalidLoanRequestException(
                    "Requested amount must not exceed ₹%.2f", MAX_REQUESTED_AMOUNT);
        }
    }
}
