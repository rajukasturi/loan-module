package com.demo.loan_module.exception;

/**
 * Exception thrown when loan request contains invalid data.
 * Used for validation failures at the input level.
 */
public class InvalidLoanRequestException extends LoanProcessingException {
    
    public InvalidLoanRequestException(String errorMessage) {
        super("LOAN-400-001", errorMessage);
    }
    
    public InvalidLoanRequestException(String errorMessage, Object... params) {
        super("LOAN-400-001", errorMessage, params);
    }
    
    public InvalidLoanRequestException(String errorMessage, Throwable cause) {
        super("LOAN-400-001", errorMessage, cause);
    }
}
