package com.demo.loan_module.service;

import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.dto.response.LoanApplicationResponse;
import com.demo.loan_module.entity.LoanApplication;

import java.util.List;
import java.util.Optional;

/**
 * Main service interface for loan application management.
 * Provides CRUD operations and business logic for loan applications.
 */
public interface LoanApplicationService {
    
    /**
     * Creates a new loan application.
     * 
     * @param request The loan application request
     * @return LoanApplicationResponse with application details
     */
    LoanApplicationResponse createLoanApplication(CreateLoanApplicationRequest request);
    
    /**
     * Retrieves a loan application by ID.
     * 
     * @param id The application ID
     * @return Optional containing the loan application if found
     */
    Optional<LoanApplication> getLoanApplicationById(Long id);
    
    /**
     * Retrieves a loan application by reference number.
     * 
     * @param referenceNumber The application reference number
     * @return Optional containing the loan application if found
     */
    Optional<LoanApplication> getLoanApplicationByReferenceNumber(String referenceNumber);
    
    /**
     * Retrieves all loan applications for a given PAN number.
     * 
     * @param panNumber The PAN number to search for
     * @return List of loan applications for the PAN
     */
    List<LoanApplication> getLoanApplicationsByPanNumber(String panNumber);
    
    /**
     * Retrieves all loan applications.
     * 
     * @return List of all loan applications
     */
    List<LoanApplication> getAllLoanApplications();
    
    /**
     * Updates a loan application.
     * 
     * @param id The application ID
     * @param application The updated application data
     * @return The updated loan application
     */
    LoanApplication updateLoanApplication(Long id, LoanApplication application);
    
    /**
     * Deletes a loan application.
     * 
     * @param id The application ID
     */
    void deleteLoanApplication(Long id);
    
    /**
     * Retrieves all loan applications by status.
     * 
     * @param status The loan status to filter by
     * @return List of loan applications with the specified status
     */
    List<LoanApplication> getLoanApplicationsByStatus(String status);
    
    /**
     * Generates an application reference number.
     * 
     * @return A unique application reference number
     */
    String generateApplicationReferenceNumber();
}
