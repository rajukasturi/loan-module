package com.demo.loan_module.dto.request;

import com.demo.loan_module.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO for creating a new loan application.
 * Used as input for the loan application API.
 */
@Data
public class CreateLoanApplicationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "PAN number must be in valid format (e.g., ABCDE1234F)")
    private String panNumber;

    @NotNull(message = "Monthly income is required")
    @Min(value = 10000, message = "Monthly income must be at least ₹10,000")
    private Double monthlyIncome;

    @NotNull(message = "Requested amount is required")
    @Min(value = 1000, message = "Requested amount must be at least ₹1,000")
    private Double requestedAmount;

    private LoanType loanType;

    @NotNull(message = "Tenure in months is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureInMonths = 12;

    // Property details for Home Loan
    private PropertyDetailsDto propertyDetails;

    // Gold details for Gold Loan
    private GoldDetailsDto goldDetails;

    // Employment details
    private EmploymentDetailsDto employmentDetails;

    /**
     * Determines the effective loan type based on the provided details.
     * Priority: GOLD_LOAN > HOME_LOAN > default (HOME_LOAN)
     *
     * @return The determined LoanType
     */
    public LoanType getEffectiveLoanType() {
        if (goldDetails != null) {
            return LoanType.GOLD_LOAN;
        } else if (propertyDetails != null) {
            return LoanType.HOME_LOAN;
        } else if (loanType != null) {
            return loanType;
        }
        return LoanType.HOME_LOAN; // Default fallback
    }

    @Data
    public static class PropertyDetailsDto {
        @Min(value = 100000, message = "Property value must be at least ₹100,000")
        private Double propertyValue;

        @Size(max = 500, message = "Property address must not exceed 500 characters")
        private String propertyAddress;

        private Boolean isSelfOccupied;
    }

    @Data
    public static class GoldDetailsDto {
        @NotNull(message = "Gold weight in grams is required for gold loan")
        @Min(value = 1, message = "Gold weight must be at least 1 gram")
        private Double goldWeightInGrams;

        @NotNull(message = "Gold carat is required for gold loan")
        @Min(value = 18, message = "Gold carat must be at least 18")
        private Integer goldCarat;

        @Size(max = 500, message = "Gold item description must not exceed 500 characters")
        private String goldItemDescription;
    }

    @Data
    public static class EmploymentDetailsDto {
        @NotBlank(message = "Employment type is required")
        private String employmentType; // SALARIED, SELF_EMPLOYED, BUSINESS, etc.

        @Size(max = 100, message = "Company name must not exceed 100 characters")
        private String companyName;

        @NotNull(message = "Years of service is required")
        @Min(value = 0, message = "Years of service must be at least 0")
        private Integer yearsOfService;
    }
}
