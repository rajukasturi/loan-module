package com.demo.loan_module.service;

import com.demo.loan_module.model.EligibilityResponse;
import org.springframework.stereotype.Service;

@Service
public class MockCibilService {

    public EligibilityResponse checkEligibility(String panNumber, Double monthlyIncome) {
        // Generate a deterministic mock score between 300 and 900 based on PAN
        int mockCibil = 300 + (Math.abs(panNumber.hashCode()) % 600);

        EligibilityResponse response = new EligibilityResponse();
        response.setCibilScore(mockCibil);

        if (mockCibil >= 700) {
            // Eligible for up to 10x monthly income
            response.setEligibleAmount(monthlyIncome * 10);
            response.setRateOfInterest(10.5);
            response.setEligible(true);
        } else {
            response.setEligibleAmount(0.0);
            response.setRateOfInterest(0.0);
            response.setEligible(false);
        }
        return response;
    }


}
