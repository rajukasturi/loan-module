package com.demo.loan_module.service.impl;

import com.demo.loan_module.model.EligibilityResponse;
import com.demo.loan_module.service.CreditBureauService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of CreditBureauService using mock CIBIL data.
 * Generates deterministic mock scores based on PAN number for testing.
 * In production, this would integrate with actual CIBIL/credit bureau APIs.
 */
@Service
public class CibilServiceImpl implements CreditBureauService {

    private static final Logger logger = LoggerFactory.getLogger(CibilServiceImpl.class);

    // CIBIL score thresholds
    private static final int EXCELLENT_SCORE = 800;
    private static final int GOOD_SCORE = 750;
    private static final int FAIR_SCORE = 700;
    private static final int MIN_ELIGIBLE_SCORE = 650;

    @Override
    public EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome) {
        logger.info("Checking CIBIL eligibility for PAN: {}", maskPan(panNumber));

        // Generate deterministic mock score based on PAN hash
        int cibilScore = calculateMockCibilScore(panNumber);

        EligibilityResponse response = new EligibilityResponse();
        response.setCibilScore(cibilScore);

        // Determine eligibility based on score
        if (cibilScore >= MIN_ELIGIBLE_SCORE) {
            double eligibleAmount = calculateEligibleAmount(cibilScore, monthlyIncome);
            double rateOfInterest = calculateRateOfInterest(cibilScore);

            response.setEligibleAmount(eligibleAmount);
            response.setRateOfInterest(rateOfInterest);
            response.setEligible(true);

            logger.info("Applicant eligible - Score: {}, Amount: ₹{}, Rate: {}%",
                    cibilScore, eligibleAmount, rateOfInterest);
        } else {
            response.setEligibleAmount(0.0);
            response.setRateOfInterest(0.0);
            response.setEligible(false);

            logger.warn("Applicant not eligible - Score: {}", cibilScore);
        }

        return response;
    }

    @Override
    public EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome,
                                                Double loanAmount, Integer tenureInMonths) {
        logger.info("Checking CIBIL eligibility with loan details - PAN: {}, Amount: ₹{}, Tenure: {} months",
                maskPan(panNumber), loanAmount, tenureInMonths);

        EligibilityResponse response = checkEligibility(panNumber, monthlyIncome);

        // For home loans, adjust eligible amount based on tenure
        // Longer tenure can reduce eligible amount
        if (tenureInMonths != null && tenureInMonths > 360) {
            // Reduce eligible amount by 10% for tenures > 30 years
            double adjustedAmount = response.getEligibleAmount() * 0.9;
            response.setEligibleAmount(adjustedAmount);
        }

        return response;
    }

    /**
     * Calculates mock CIBIL score from PAN number.
     * Generates a deterministic score between 300-900.
     */
    private int calculateMockCibilScore(String panNumber) {
        if (panNumber == null || panNumber.isEmpty()) {
            return 300;
        }
        // Use hash to generate deterministic score
        int hash = Math.abs(panNumber.hashCode());
        return 300 + (hash % 601); // 300-900 range
    }

    /**
     * Calculates eligible amount based on CIBIL score and income.
     */
    private double calculateEligibleAmount(int cibilScore, Double monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome <= 0) {
            return 0.0;
        }

        double multiplier;
        if (cibilScore >= EXCELLENT_SCORE) {
            multiplier = 15.0; // 15x monthly income
        } else if (cibilScore >= GOOD_SCORE) {
            multiplier = 12.0; // 12x monthly income
        } else if (cibilScore >= FAIR_SCORE) {
            multiplier = 10.0; // 10x monthly income
        } else {
            multiplier = 8.0; // 8x for scores between 650-699
        }

        return monthlyIncome * multiplier;
    }

    /**
     * Calculates rate of interest based on CIBIL score.
     */
    private double calculateRateOfInterest(int cibilScore) {
        if (cibilScore >= EXCELLENT_SCORE) {
            return 8.5; // 8.5%
        } else if (cibilScore >= GOOD_SCORE) {
            return 9.5; // 9.5%
        } else if (cibilScore >= FAIR_SCORE) {
            return 10.5; // 10.5%
        } else if (cibilScore >= MIN_ELIGIBLE_SCORE) {
            return 12.5; // 12.5%
        } else {
            return 0.0;
        }
    }

    /**
     * Masks PAN number for logging (show only first and last character).
     */
    private String maskPan(String panNumber) {
        if (panNumber == null || panNumber.length() <= 2) {
            return panNumber;
        }
        return panNumber.charAt(0) + "*****" + panNumber.charAt(panNumber.length() - 1);
    }
}
