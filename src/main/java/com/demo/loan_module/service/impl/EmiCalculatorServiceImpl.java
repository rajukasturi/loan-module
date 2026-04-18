package com.demo.loan_module.service.impl;

import com.demo.loan_module.enums.LoanType;
import com.demo.loan_module.service.EmiCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of EMI calculation service.
 * Uses standard financial formulas to calculate loan EMIs and related fees.
 */
@Service
public class EmiCalculatorServiceImpl implements EmiCalculatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmiCalculatorServiceImpl.class);
    
    // Processing fee percentages by loan type
    private static final double HOME_LOAN_PROCESSING_FEE_PERCENT = 0.01; // 1%
    private static final double PERSONAL_LOAN_PROCESSING_FEE_PERCENT = 0.02; // 2%
    private static final double CAR_LOAN_PROCESSING_FEE_PERCENT = 0.015; // 1.5%
    private static final double EDUCATION_LOAN_PROCESSING_FEE_PERCENT = 0.01; // 1%
    
    // Maximum processing fee caps
    private static final double MAX_PROCESSING_FEE_HOME_LOAN = 10000.0; // ₹10,000
    private static final double MAX_PROCESSING_FEE_PERSONAL_LOAN = 5000.0; // ₹5,000
    
    @Override
    public double calculateEmi(double principal, double annualRate, int tenureInMonths) {
        logger.debug("Calculating EMI - Principal: ₹{}, Rate: {}%, Tenure: {} months", 
                    principal, annualRate, tenureInMonths);
        
        if (principal <= 0 || annualRate <= 0 || tenureInMonths <= 0) {
            return 0.0;
        }
        
        // Convert annual rate to monthly rate
        double monthlyRate = annualRate / (12 * 100);
        
        // EMI formula: P * r * (1 + r)^n / ((1 + r)^n - 1)
        // where P = principal, r = monthly rate, n = number of months
        double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths) /
                    (Math.pow(1 + monthlyRate, tenureInMonths) - 1);
        
        logger.debug("Calculated EMI: ₹{}", emi);
        return Math.round(emi * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    @Override
    public double calculateEmi(double principal, double annualRate, int tenureInMonths, 
                               String loanType) {
        // For now, use the same calculation for all loan types
        // In future, can have different calculation methods per loan type
        return calculateEmi(principal, annualRate, tenureInMonths);
    }
    
    @Override
    public double calculateProcessingFee(double loanAmount, String loanType) {
        logger.debug("Calculating processing fee - Amount: ₹{}, Type: {}", loanAmount, loanType);
        
        if (loanAmount <= 0) {
            return 0.0;
        }
        
        double feePercent;
        double maxFeeCap = 0.0;
        
        try {
            LoanType type = LoanType.fromCode(loanType);
            switch (type) {
                case HOME_LOAN:
                    feePercent = HOME_LOAN_PROCESSING_FEE_PERCENT;
                    maxFeeCap = MAX_PROCESSING_FEE_HOME_LOAN;
                    break;
                case PERSONAL_LOAN:
                    feePercent = PERSONAL_LOAN_PROCESSING_FEE_PERCENT;
                    maxFeeCap = MAX_PROCESSING_FEE_PERSONAL_LOAN;
                    break;
                case CAR_LOAN:
                    feePercent = CAR_LOAN_PROCESSING_FEE_PERCENT;
                    break;
                case EDUCATION_LOAN:
                    feePercent = EDUCATION_LOAN_PROCESSING_FEE_PERCENT;
                    break;
                default:
                    feePercent = HOME_LOAN_PROCESSING_FEE_PERCENT;
            }
        } catch (IllegalArgumentException e) {
            // Default to home loan fee for unknown types
            feePercent = HOME_LOAN_PROCESSING_FEE_PERCENT;
            maxFeeCap = MAX_PROCESSING_FEE_HOME_LOAN;
        }
        
        double fee = loanAmount * feePercent;
        
        // Apply maximum cap if applicable
        if (maxFeeCap > 0 && fee > maxFeeCap) {
            fee = maxFeeCap;
        }
        
        logger.debug("Calculated processing fee: ₹{}", fee);
        return Math.round(fee * 100.0) / 100.0;
    }
    
    @Override
    public double calculateTotalInterest(double principal, double annualRate, int tenureInMonths) {
        if (principal <= 0 || annualRate <= 0 || tenureInMonths <= 0) {
            return 0.0;
        }
        
        double totalPayment = calculateEmi(principal, annualRate, tenureInMonths) * tenureInMonths;
        return Math.round((totalPayment - principal) * 100.0) / 100.0;
    }
}
