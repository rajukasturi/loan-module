package com.demo.loan_module.enums;

/**
 * Enum representing the status of a loan application.
 * Follows production standards for clear state management.
 */
public enum LoanStatus {
    
    /** Application received but not yet processed */
    PENDING("PENDING"),
    
    /** Application approved and ready for disbursement */
    APPROVED("APPROVED"),
    
    /** Application rejected due to eligibility failure */
    REJECTED("REJECTED"),
    
    /** Application approved but requires additional documentation */
    APPROVED_WITH_CONDITIONS("APPROVED_WITH_CONDITIONS");
    
    private final String value;
    
    LoanStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * Factory method to get LoanStatus from string value.
     */
    public static LoanStatus fromValue(String value) {
        for (LoanStatus status : LoanStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown loan status: " + value);
    }
}
