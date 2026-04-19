package com.demo.loan_module.dto.response;

import com.demo.loan_module.enums.LoanStatus;
import com.demo.loan_module.enums.LoanType;
import lombok.Data;

import java.util.Date;

/**
 * Response DTO for loan application.
 * Contains all relevant information to return to the client.
 */
@Data
public class LoanApplicationResponse {

    private Long applicationId;
    private String applicationReferenceNumber;
    private String fullName;
    private String panNumber;
    private LoanType loanType;
    private Double monthlyIncome;
    private Double requestedAmount;
    private Integer tenureInMonths;

    // Credit bureau details
    private Integer cibilScore;
    private Double eligibleAmount;
    private Double rateOfInterest;

    // Approval details
    private LoanStatus status;
    private Double sanctionedAmount;
    private Double processingFee;
    private Double monthlyEmi;

    // Property details (for home loans)
    private Double propertyValue;
    private String propertyAddress;
    private Boolean isSelfOccupied;

    // Gold details (for gold loans)
    private Double goldWeightInGrams;
    private Integer goldCarat;
    private String goldItemDescription;

    // Audit information
    private Date createdAt;
    private String message;
}
