package com.demo.loan_module.entity;

import com.demo.loan_module.enums.LoanStatus;
import com.demo.loan_module.enums.LoanType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.util.Date;

/**
 * Entity representing a loan application.
 * Contains all fields required for loan processing and audit tracking.
 */
@Entity
@Data
public class LoanApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false, unique = true)
    private String panNumber;
    
    @Column(nullable = false)
    private Double monthlyIncome;
    
    @Column(nullable = false)
    private Double requestedAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType = LoanType.HOME_LOAN;
    
    @Column(nullable = false)
    private Integer tenureInMonths;
    
    // Fields populated by the Credit Bureau Service
    private Integer cibilScore;
    private Double eligibleAmount;
    private Double rateOfInterest;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;
    
    // Home Loan specific fields
    private Double propertyValue;
    private String propertyAddress;
    private Boolean isSelfOccupied;
    
    // Employment details
    private String employmentType;
    private String companyName;
    private Integer yearsOfService;
    
    // Approval details
    private Double sanctionedAmount;
    private Double processingFee;
    private Double monthlyEmi;
    
    // Audit fields
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    private String createdBy;
    private String updatedBy;
    
    // Calculator flag to check if EMIs are calculated
    private Boolean isEmiCalculated = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    /**
     * Checks if this is a home loan application.
     */
    public boolean isHomeLoan() {
        return loanType != null && loanType.isHomeLoan();
    }
    
    /**
     * Checks if the application is approved.
     */
    public boolean isApproved() {
        return status != null && status == LoanStatus.APPROVED;
    }
    
    /**
     * Checks if the application is rejected.
     */
    public boolean isRejected() {
        return status != null && status == LoanStatus.REJECTED;
    }
}
