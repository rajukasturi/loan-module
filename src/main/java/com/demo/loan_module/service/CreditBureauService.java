package com.demo.loan_module.service;

import com.demo.loan_module.model.EligibilityResponse;

/**
 * Service interface for interacting with credit bureau systems.
 * Abstracts the credit scoring implementation allowing for multiple providers.
 */
public interface CreditBureauService {
    
    /**
     * Checks eligibility for a loan based on applicant's PAN and income.
     * 
     * @param panNumber The PAN number of the applicant
     * @param monthlyIncome The monthly income of the applicant
     * @return EligibilityResponse containing CIBIL score and eligibility details
     */
    EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome);
    
    /**
     * Checks eligibility with additional loan-specific parameters.
     * 
     * @param panNumber The PAN number of the applicant
     * @param monthlyIncome The monthly income of the applicant
     * @param loanAmount The requested loan amount
     * @param tenureInMonths The loan tenure in months
     * @return EligibilityResponse containing CIBIL score and eligibility details
     */
    default EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome, 
                                                  Double loanAmount, Integer tenureInMonths) {
        // Default implementation ignores loan amount and tenure
        return checkEligibility(panNumber, monthlyIncome);
    }
}
