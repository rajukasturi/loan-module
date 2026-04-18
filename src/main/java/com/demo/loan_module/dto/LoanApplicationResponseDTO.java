package com.demo.loan_module.dto;

import lombok.Data;

@Data
public class LoanApplicationResponseDTO {
    private Long applicationId;
    private String fullName;
    private Integer cibilScore;
    private Double eligibleAmount;
    private Double rateOfInterest;
    private String status;
}
