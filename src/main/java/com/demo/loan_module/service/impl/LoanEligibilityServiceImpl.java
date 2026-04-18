package com.demo.loan_module.service.impl;

import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.enums.LoanType;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.model.EligibilityResponse;
import com.demo.loan_module.service.CreditBureauService;
import com.demo.loan_module.service.LoanEligibilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of loan eligibility service.
 * Handles all eligibility checks including credit bureau and business rules.
 */
@Service
public class LoanEligibilityServiceImpl implements LoanEligibilityService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanEligibilityServiceImpl.class);
    
    private final CreditBureauService creditBureauService;
    
    public LoanEligibilityServiceImpl(CreditBureauService creditBureauService) {
        this.creditBureauService = creditBureauService;
    }
    
    @Override
    public EligibilityResponse checkEligibility(CreateLoanApplicationRequest request) {
        logger.info("Checking loan eligibility for applicant: {}", 
                   request.getFullName());
        
        if (request == null) {
            throw new IllegalArgumentException("Loan request cannot be null");
        }
        
        // Get eligibility from credit bureau
        EligibilityResponse response = creditBureauService.checkEligibility(
            request.getPanNumber(), 
            request.getMonthlyIncome(),
            request.getRequestedAmount(),
            request.getTenureInMonths()
        );
        
        logger.debug("Credit bureau response - Score: {}, Eligible: {}, Amount: ₹{}", 
                    response.getCibilScore(), response.isEligible(), response.getEligibleAmount());
        
        return response;
    }
    
    @Override
    public EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome) {
        return creditBureauService.checkEligibility(panNumber, monthlyIncome);
    }
    
    @Override
    public boolean isAmountWithinLimit(double requestedAmount, EligibilityResponse eligibilityResponse) {
        if (eligibilityResponse == null || !eligibilityResponse.isEligible()) {
            logger.warn("Eligibility check failed - applicant not eligible");
            return false;
        }
        
        double eligibleAmount = eligibilityResponse.getEligibleAmount();
        boolean withinLimit = requestedAmount <= eligibleAmount;
        
        logger.debug("Amount check - Requested: ₹{}, Eligible: ₹{}, Within limit: {}", 
                    requestedAmount, eligibleAmount, withinLimit);
        
        return withinLimit;
    }
    
    @Override
    public boolean isHomeLoanEligible(LoanApplication application) {
        if (application == null || !application.isHomeLoan()) {
            return true; // Not a home loan, skip home loan specific checks
        }
        
        logger.info("Checking home loan specific eligibility for application: {}", application.getId());
        
        // Home loan specific checks
        // 1. Property value should be at least 1.5x the loan amount
        if (application.getPropertyValue() != null && application.getRequestedAmount() != null) {
            double ltvRatio = application.getRequestedAmount() / application.getPropertyValue();
            if (ltvRatio > 0.8) { // 80% LTV ratio
                logger.warn("Home loan LTV ratio exceeded: {}% > 80%", ltvRatio * 100);
                return false;
            }
        }
        
        // 2. For salaried individuals, minimum income requirements
        if ("SALARIED".equalsIgnoreCase(application.getEmploymentType())) {
            if (application.getMonthlyIncome() != null && application.getMonthlyIncome() < 25000) {
                logger.warn("Salaried applicant income below threshold: ₹{}", 
                           application.getMonthlyIncome());
                return false;
            }
        }
        
        // 3. Tenure validation (max 30 years for home loans)
        if (application.getTenureInMonths() != null && application.getTenureInMonths() > 360) {
            logger.warn("Home loan tenure exceeds maximum: {} months > 360", 
                       application.getTenureInMonths());
            return false;
        }
        
        logger.info("Home loan eligibility checks passed for application: {}", application.getId());
        return true;
    }
}
