package com.demo.loan_module.dto.request;

import com.demo.loan_module.enums.LoanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    
    @NotNull(message = "Loan type is required")
    private LoanType loanType = LoanType.HOME_LOAN;
    
    @NotNull(message = "Tenure in months is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureInMonths = 12;
    
    // Property details for Home Loan
    private PropertyDetailsDto propertyDetails;
    
    // Employment details
    private EmploymentDetailsDto employmentDetails;
    
    @Data
    public static class PropertyDetailsDto {
        @Min(value = 100000, message = "Property value must be at least ₹100,000")
        private Double propertyValue;
        
        @Size(max = 500, message = "Property address must not exceed 500 characters")
        private String propertyAddress;
        
        private Boolean isSelfOccupied;
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
