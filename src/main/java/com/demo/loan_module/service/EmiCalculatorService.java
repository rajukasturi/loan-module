package com.demo.loan_module.service;

/**
 * Service for calculating EMI (Equated Monthly Installment) for loans.
 * Provides methods to calculate monthly payments based on principal, rate, and tenure.
 */
public interface EmiCalculatorService {

    /**
     * Calculates EMI for a loan.
     *
     * @param principal      The loan principal amount
     * @param annualRate     The annual interest rate (in percentage)
     * @param tenureInMonths The loan tenure in months
     * @return The monthly EMI amount
     */
    double calculateEmi(double principal, double annualRate, int tenureInMonths);

    /**
     * Calculates EMI with loan type specific rules.
     *
     * @param principal      The loan principal amount
     * @param annualRate     The annual interest rate (in percentage)
     * @param tenureInMonths The loan tenure in months
     * @param loanType       The type of loan (can affect calculation)
     * @return The monthly EMI amount
     */
    double calculateEmi(double principal, double annualRate, int tenureInMonths,
                        String loanType);

    /**
     * Calculates processing fee for a loan.
     *
     * @param loanAmount The loan amount
     * @param loanType   The type of loan
     * @return The processing fee amount
     */
    double calculateProcessingFee(double loanAmount, String loanType);

    /**
     * Calculates total interest payable over the loan tenure.
     *
     * @param principal      The loan principal amount
     * @param annualRate     The annual interest rate (in percentage)
     * @param tenureInMonths The loan tenure in months
     * @return The total interest amount
     */
    double calculateTotalInterest(double principal, double annualRate, int tenureInMonths);
}
