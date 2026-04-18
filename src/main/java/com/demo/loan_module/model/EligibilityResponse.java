package com.demo.loan_module.model;

import lombok.Data;

@Data
public class EligibilityResponse {
    private Integer cibilScore;
    private Double eligibleAmount;
    private Double rateOfInterest;
    private boolean isEligible;
}
