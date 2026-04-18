package com.demo.loan_module.model;

import lombok.Data;

@Data
public class LoanRequest {
    private String fullName;
    private String panNumber;
    private Double monthlyIncome;
    private Double requestedAmount;
}