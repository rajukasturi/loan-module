package com.demo.loan_module.exception;

/**
 * Exception thrown when loan eligibility criteria are not met.
 * Used when CIBIL score or other eligibility rules fail.
 */
public class LoanEligibilityException extends LoanProcessingException {

    private final int cibilScore;
    private final double eligibleAmount;

    public LoanEligibilityException(String message, int cibilScore, double eligibleAmount) {
        super("LOAN-400-002", message, cibilScore, eligibleAmount);
        this.cibilScore = cibilScore;
        this.eligibleAmount = eligibleAmount;
    }

    public LoanEligibilityException(String message, int cibilScore, double eligibleAmount, Throwable cause) {
        super("LOAN-400-002", message, cause);
        this.cibilScore = cibilScore;
        this.eligibleAmount = eligibleAmount;
    }

    public int getCibilScore() {
        return cibilScore;
    }

    public double getEligibleAmount() {
        return eligibleAmount;
    }
}
