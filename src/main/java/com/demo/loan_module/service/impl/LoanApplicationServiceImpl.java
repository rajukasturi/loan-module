package com.demo.loan_module.service.impl;

import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.dto.response.LoanApplicationResponse;
import com.demo.loan_module.enums.LoanStatus;
import com.demo.loan_module.enums.LoanType;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.exception.InvalidLoanRequestException;
import com.demo.loan_module.exception.LoanEligibilityException;
import com.demo.loan_module.exception.ResourceNotFoundException;
import com.demo.loan_module.model.EligibilityResponse;
import com.demo.loan_module.repository.LoanRepository;
import com.demo.loan_module.service.EmiCalculatorService;
import com.demo.loan_module.service.LoanApplicationService;
import com.demo.loan_module.service.LoanEligibilityService;
import com.demo.loan_module.validation.LoanRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of LoanApplicationService.
 * Handles all loan application operations including creation, retrieval, and processing.
 */
@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationServiceImpl.class);
    
    private final LoanRepository loanRepository;
    private final LoanEligibilityService loanEligibilityService;
    private final EmiCalculatorService emiCalculatorService;
    private final LoanRequestValidator loanRequestValidator;
    
    public LoanApplicationServiceImpl(LoanRepository loanRepository,
                                      LoanEligibilityService loanEligibilityService,
                                      EmiCalculatorService emiCalculatorService,
                                      LoanRequestValidator loanRequestValidator) {
        this.loanRepository = loanRepository;
        this.loanEligibilityService = loanEligibilityService;
        this.emiCalculatorService = emiCalculatorService;
        this.loanRequestValidator = loanRequestValidator;
    }
    
    @Override
    @Transactional
    public LoanApplicationResponse createLoanApplication(CreateLoanApplicationRequest request) {
        logger.info("Starting loan application process for: {}", request.getFullName());
        
        // Step 1: Validate the request
        logger.debug("Validating loan request...");
        try {
            loanRequestValidator.validate(mapRequestToModel(request));
        } catch (InvalidLoanRequestException e) {
            logger.error("Loan request validation failed: {}", e.getMessage());
            throw e;
        }
        
        // Step 2: Check eligibility
        logger.debug("Checking eligibility...");
        EligibilityResponse eligibilityResponse = loanEligibilityService.checkEligibility(request);
        
        if (!eligibilityResponse.isEligible()) {
            logger.error("Applicant not eligible - CIBIL Score: {}", eligibilityResponse.getCibilScore());
            throw new LoanEligibilityException(
                "Loan eligibility check failed", 
                eligibilityResponse.getCibilScore(), 
                eligibilityResponse.getEligibleAmount()
            );
        }
        
        // Step 3: Check if amount is within eligible limit
        boolean amountWithinLimit = loanEligibilityService.isAmountWithinLimit(
            request.getRequestedAmount(), eligibilityResponse);
        
        if (!amountWithinLimit) {
            logger.error("Requested amount ₹{} exceeds eligible amount ₹{}", 
                        request.getRequestedAmount(), eligibilityResponse.getEligibleAmount());
            throw new LoanEligibilityException(
                "Requested amount exceeds eligible limit",
                eligibilityResponse.getCibilScore(),
                eligibilityResponse.getEligibleAmount()
            );
        }
        
        // Step 4: Map request to entity
        logger.debug("Mapping request to entity...");
        LoanApplication application = mapRequestToEntity(request, eligibilityResponse);
        
        // Step 5: Generate reference number
        String referenceNumber = generateApplicationReferenceNumber();
        application.setCreatedAt(new Date());
        application.setUpdatedAt(new Date());
        
        // Step 6: Calculate EMI and other financials
        calculateAndSetFinancials(application, eligibilityResponse);
        
        // Step 7: Determine status
        determineAndSetStatus(application);
        
        // Step 8: Save to database
        logger.debug("Saving loan application to database...");
        LoanApplication savedApplication = loanRepository.save(application);
        
        // Step 9: Map to response DTO
        LoanApplicationResponse response = mapEntityToResponse(savedApplication);
        response.setApplicationReferenceNumber(referenceNumber);
        response.setMessage("Loan application submitted successfully");
        
        logger.info("Loan application created successfully - ID: {}, Reference: {}", 
                   savedApplication.getId(), referenceNumber);
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LoanApplication> getLoanApplicationById(Long id) {
        logger.debug("Retrieving loan application by ID: {}", id);
        return loanRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LoanApplication> getLoanApplicationByReferenceNumber(String referenceNumber) {
        logger.debug("Retrieving loan application by reference: {}", referenceNumber);
        return loanRepository.findById(Long.parseLong(referenceNumber.split("-")[1]));
        // Note: In production, you would query by referenceNumber field
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanApplication> getLoanApplicationsByPanNumber(String panNumber) {
        logger.debug("Retrieving loan applications by PAN: {}", panNumber);
        return loanRepository.findByPanNumber(panNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanApplication> getAllLoanApplications() {
        logger.debug("Retrieving all loan applications");
        return loanRepository.findAll();
    }
    
    @Override
    @Transactional
    public LoanApplication updateLoanApplication(Long id, LoanApplication application) {
        logger.debug("Updating loan application with ID: {}", id);
        
        if (!loanRepository.existsById(id)) {
            throw new ResourceNotFoundException("LoanApplication", id);
        }
        
        application.setId(id);
        application.setUpdatedAt(new Date());
        
        LoanApplication updated = loanRepository.save(application);
        logger.info("Loan application updated - ID: {}", id);
        return updated;
    }
    
    @Override
    @Transactional
    public void deleteLoanApplication(Long id) {
        logger.debug("Deleting loan application with ID: {}", id);
        
        if (!loanRepository.existsById(id)) {
            throw new ResourceNotFoundException("LoanApplication", id);
        }
        
        loanRepository.deleteById(id);
        logger.info("Loan application deleted - ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoanApplication> getLoanApplicationsByStatus(String status) {
        logger.debug("Retrieving loan applications by status: {}", status);
        try {
            LoanStatus loanStatus = LoanStatus.fromValue(status);
            return loanRepository.findByStatus(loanStatus);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value: {}", status);
            return loanRepository.findAll().stream()
                .filter(app -> app.getStatus().getValue().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }
    }
    
    @Override
    public String generateApplicationReferenceNumber() {
        // Generate a unique reference number: LOAN-YYYYMMDD-UUID
        String datePart = java.time.LocalDate.now().toString().replace("-", "");
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "LOAN-" + datePart + "-" + uuidPart;
    }
    
    /**
     * Maps CreateLoanApplicationRequest to LoanApplication entity.
     */
    private LoanApplication mapRequestToEntity(CreateLoanApplicationRequest request,
                                               EligibilityResponse eligibilityResponse) {
        LoanApplication application = new LoanApplication();
        
        // Basic information
        application.setFullName(request.getFullName());
        application.setPanNumber(request.getPanNumber());
        application.setMonthlyIncome(request.getMonthlyIncome());
        application.setRequestedAmount(request.getRequestedAmount());
        application.setLoanType(request.getLoanType() != null ? request.getLoanType() : LoanType.HOME_LOAN);
        application.setTenureInMonths(request.getTenureInMonths());
        
        // Credit bureau information
        application.setCibilScore(eligibilityResponse.getCibilScore());
        application.setEligibleAmount(eligibilityResponse.getEligibleAmount());
        application.setRateOfInterest(eligibilityResponse.getRateOfInterest());
        
        // Property details (for home loans)
        if (request.getPropertyDetails() != null) {
            application.setPropertyValue(request.getPropertyDetails().getPropertyValue());
            application.setPropertyAddress(request.getPropertyDetails().getPropertyAddress());
            application.setIsSelfOccupied(request.getPropertyDetails().getIsSelfOccupied());
        }
        
        // Employment details
        if (request.getEmploymentDetails() != null) {
            application.setEmploymentType(request.getEmploymentDetails().getEmploymentType());
            application.setCompanyName(request.getEmploymentDetails().getCompanyName());
            application.setYearsOfService(request.getEmploymentDetails().getYearsOfService());
        }
        
        return application;
    }
    
    /**
     * Maps CreateLoanApplicationRequest to LoanRequest for validation.
     */
    private com.demo.loan_module.model.LoanRequest mapRequestToModel(CreateLoanApplicationRequest request) {
        com.demo.loan_module.model.LoanRequest model = new com.demo.loan_module.model.LoanRequest();
        model.setFullName(request.getFullName());
        model.setPanNumber(request.getPanNumber());
        model.setMonthlyIncome(request.getMonthlyIncome());
        model.setRequestedAmount(request.getRequestedAmount());
        return model;
    }
    
    /**
     * Calculates and sets financial details on the application.
     */
    private void calculateAndSetFinancials(LoanApplication application, 
                                          EligibilityResponse eligibilityResponse) {
        double sanctionedAmount = application.getRequestedAmount();
        double rateOfInterest = eligibilityResponse.getRateOfInterest();
        int tenureInMonths = application.getTenureInMonths();
        
        // Calculate EMI
        double emi = emiCalculatorService.calculateEmi(
            sanctionedAmount, rateOfInterest, tenureInMonths);
        application.setMonthlyEmi(emi);
        application.setSanctionedAmount(sanctionedAmount);
        
        // Calculate processing fee
        double processingFee = emiCalculatorService.calculateProcessingFee(
            sanctionedAmount, application.getLoanType().getCode());
        application.setProcessingFee(processingFee);
        
        application.setIsEmiCalculated(true);
        
        logger.debug("Calculated financials - EMI: ₹{}, Processing Fee: ₹{}, Sanctioned: ₹{}", 
                    emi, processingFee, sanctionedAmount);
    }
    
    /**
     * Determines and sets the application status.
     */
    private void determineAndSetStatus(LoanApplication application) {
        // Check if home loan specific conditions are met
        boolean homeLoanEligible = loanEligibilityService.isHomeLoanEligible(application);
        
        if (!homeLoanEligible && application.isHomeLoan()) {
            application.setStatus(LoanStatus.REJECTED);
            logger.warn("Home loan specific conditions not met - Application: {}", application.getId());
            return;
        }
        
        // Default: If we reach here, the loan is approved
        application.setStatus(LoanStatus.APPROVED);
        logger.info("Application approved - ID: {}", application.getId());
    }
    
    /**
     * Maps LoanApplication entity to LoanApplicationResponse DTO.
     */
    private LoanApplicationResponse mapEntityToResponse(LoanApplication application) {
        LoanApplicationResponse response = new LoanApplicationResponse();
        
        response.setApplicationId(application.getId());
        response.setFullName(application.getFullName());
        response.setPanNumber(application.getPanNumber());
        response.setLoanType(application.getLoanType());
        response.setMonthlyIncome(application.getMonthlyIncome());
        response.setRequestedAmount(application.getRequestedAmount());
        response.setTenureInMonths(application.getTenureInMonths());
        
        // Credit bureau details
        response.setCibilScore(application.getCibilScore());
        response.setEligibleAmount(application.getEligibleAmount());
        response.setRateOfInterest(application.getRateOfInterest());
        
        // Approval details
        response.setStatus(application.getStatus());
        response.setSanctionedAmount(application.getSanctionedAmount());
        response.setProcessingFee(application.getProcessingFee());
        response.setMonthlyEmi(application.getMonthlyEmi());
        
        // Property details
        response.setPropertyValue(application.getPropertyValue());
        response.setPropertyAddress(application.getPropertyAddress());
        response.setIsSelfOccupied(application.getIsSelfOccupied());
        
        // Audit information
        response.setCreatedAt(application.getCreatedAt());
        
        return response;
    }
}
