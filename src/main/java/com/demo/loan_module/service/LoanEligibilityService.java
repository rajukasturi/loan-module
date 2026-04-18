package com.demo.loan_module.service;

import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.model.EligibilityResponse;

/**
 * Service for determining loan eligibility.
 * Combines credit bureau checks with business rule validations.
 */
public interface LoanEligibilityService {
    
    /**
     * Checks if an applicant is eligible for a loan.
     * 
     * @param request The loan application request
     * @return EligibilityResponse with score and eligibility details
     */
    EligibilityResponse checkEligibility(CreateLoanApplicationRequest request);
    
    /**
     * Checks if an applicant is eligible based on basic parameters.
     * 
     * @param panNumber The PAN number
     * @param monthlyIncome The monthly income
     * @return EligibilityResponse with score and eligibility details
     */
    EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome);
    
    /**
     * Validates if requested amount is within eligible limits.
     * 
     * @param requestedAmount The requested loan amount
     * @param eligibilityResponse The eligibility response from credit bureau
     * @return true if amount is within limits, false otherwise
     */
    boolean isAmountWithinLimit(double requestedAmount, EligibilityResponse eligibilityResponse);
    
    /**
     * Determines if home loan specific conditions are met.
     * 
     * @param application The loan application
     * @return true if home loan conditions are satisfied
     */
    boolean isHomeLoanEligible(LoanApplication application);
}
