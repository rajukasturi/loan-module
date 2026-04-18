package com.demo.loan_module.enums;

/**
 * Enum representing different types of loans supported by the system.
 * Extensible for future loan products.
 */
public enum LoanType {
    
    /** Home loan for purchasing residential property */
    HOME_LOAN("HOME_LOAN", "Home Loan"),
    
    /** Personal loan for individual needs */
    PERSONAL_LOAN("PERSONAL_LOAN", "Personal Loan"),
    
    /** Car loan for vehicle financing */
    CAR_LOAN("CAR_LOAN", "Car Loan"),
    
    /** Education loan for academic purposes */
    EDUCATION_LOAN("EDUCATION_LOAN", "Education Loan");
    
    private final String code;
    private final String displayName;
    
    LoanType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    /**
     * Factory method to get LoanType from code.
     */
    public static LoanType fromCode(String code) {
        for (LoanType type : LoanType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown loan type code: " + code);
    }
    
    /**
     * Check if the loan type is a home loan.
     */
    public boolean isHomeLoan() {
        return this == HOME_LOAN;
    }
}
