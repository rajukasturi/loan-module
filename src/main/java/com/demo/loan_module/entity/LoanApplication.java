package com.demo.loan_module.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String panNumber;
    private Double monthlyIncome;
    private Double requestedAmount;

    // Fields populated by the Mock API
    private Integer cibilScore;
    private Double eligibleAmount;
    private Double rateOfInterest;
    private String status; // APPROVED or REJECTED
}