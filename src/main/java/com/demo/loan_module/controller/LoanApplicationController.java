package com.demo.loan_module.controller;

import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.dto.response.ApiResponse;
import com.demo.loan_module.dto.response.LoanApplicationResponse;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for loan application operations.
 * Provides endpoints for creating, retrieving, updating, and deleting loan applications.
 * Includes API versioning for backward compatibility.
 */
@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan Applications", description = "APIs for managing loan applications")
public class LoanApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationController.class);

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    /**
     * Creates a new loan application.
     *
     * @param request The loan application request
     * @return ResponseEntity containing the created loan application response
     */
    @PostMapping("/apply")
    @Operation(
            summary = "Create a new loan application",
            description = "Submits a new loan application and returns the application details",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Loan application created successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or eligibility failed"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> applyForLoan(
            @Valid @RequestBody CreateLoanApplicationRequest request) {

        logger.info("Received loan application request for: {}", request.getFullName());

        LoanApplicationResponse response = loanApplicationService.createLoanApplication(request);

        ApiResponse<LoanApplicationResponse> apiResponse =
                ApiResponse.success(
                        response,
                        "Loan application submitted successfully"
                );

        logger.info("Loan application created - ID: {}, Reference: {}",
                response.getApplicationId(), response.getApplicationReferenceNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Retrieves a loan application by ID.
     *
     * @param id The application ID
     * @return ResponseEntity containing the loan application
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get loan application by ID",
            description = "Retrieves a loan application by its unique identifier",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan application found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan application not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getLoanApplication(
            @Parameter(description = "The ID of the loan application")
            @PathVariable Long id) {

        logger.debug("Retrieving loan application by ID: {}", id);

        Optional<LoanApplication> application = loanApplicationService.getLoanApplicationById(id);

        if (application.isPresent()) {
            LoanApplicationResponse response = mapToResponse(application.get());
            ApiResponse<LoanApplicationResponse> apiResponse =
                    ApiResponse.success(
                            response,
                            "Loan application retrieved successfully"
                    );
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<LoanApplicationResponse> apiResponse =
                    ApiResponse.error(
                            "Loan application not found with id: " + id,
                            "404"
                    );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * Retrieves all loan applications for a specific PAN number.
     *
     * @param panNumber The PAN number to search for
     * @return ResponseEntity containing list of loan applications
     */
    @GetMapping("/pan/{panNumber}")
    @Operation(
            summary = "Get loan applications by PAN number",
            description = "Retrieves all loan applications associated with a PAN number",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan applications found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No loan applications found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getLoanApplicationsByPan(
            @Parameter(description = "The PAN number to search for")
            @PathVariable String panNumber) {

        logger.debug("Retrieving loan applications by PAN: {}", panNumber);

        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByPanNumber(panNumber);

        List<LoanApplicationResponse> responses = applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        ApiResponse<List<LoanApplicationResponse>> apiResponse =
                ApiResponse.success(
                        responses,
                        "Found " + responses.size() + " loan application(s) for PAN: " + panNumber
                );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Retrieves all loan applications.
     *
     * @return ResponseEntity containing list of all loan applications
     */
    @GetMapping
    @Operation(
            summary = "Get all loan applications",
            description = "Retrieves all loan applications (admin access recommended)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan applications retrieved",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getAllLoanApplications() {
        logger.debug("Retrieving all loan applications");

        List<LoanApplication> applications = loanApplicationService.getAllLoanApplications();

        List<LoanApplicationResponse> responses = applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        ApiResponse<List<LoanApplicationResponse>> apiResponse =
                ApiResponse.success(
                        responses,
                        "Retrieved " + responses.size() + " loan application(s)"
                );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Retrieves loan applications by status.
     *
     * @param status The loan status to filter by
     * @return ResponseEntity containing list of loan applications with the specified status
     */
    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get loan applications by status",
            description = "Retrieves all loan applications with a specific status",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan applications found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> getLoanApplicationsByStatus(
            @Parameter(description = "The status to filter by (APPROVED, REJECTED, PENDING, etc.)")
            @PathVariable String status) {

        logger.debug("Retrieving loan applications by status: {}", status);

        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByStatus(status);

        List<LoanApplicationResponse> responses = applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        ApiResponse<List<LoanApplicationResponse>> apiResponse =
                ApiResponse.success(
                        responses,
                        "Found " + responses.size() + " loan application(s) with status: " + status
                );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Updates a loan application.
     *
     * @param id      The application ID
     * @param request The updated loan application data
     * @return ResponseEntity containing the updated loan application response
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update loan application",
            description = "Updates an existing loan application",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan application updated successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan application not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> updateLoanApplication(
            @Parameter(description = "The ID of the loan application to update")
            @PathVariable Long id,
            @Valid @RequestBody CreateLoanApplicationRequest request) {

        logger.debug("Updating loan application with ID: {}", id);

        Optional<LoanApplication> existingApp = loanApplicationService.getLoanApplicationById(id);
        if (existingApp.isEmpty()) {
            ApiResponse<LoanApplicationResponse> apiResponse =
                    ApiResponse.error(
                            "Loan application not found with id: " + id,
                            "404"
                    );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        LoanApplication application = new LoanApplication();
        application.setId(id);
        application.setFullName(request.getFullName());
        application.setPanNumber(request.getPanNumber());
        application.setMonthlyIncome(request.getMonthlyIncome());
        application.setRequestedAmount(request.getRequestedAmount());
        application.setLoanType(request.getLoanType() != null ? request.getLoanType() : existingApp.get().getLoanType());
        application.setTenureInMonths(request.getTenureInMonths());

        application.setCibilScore(existingApp.get().getCibilScore());
        application.setEligibleAmount(existingApp.get().getEligibleAmount());
        application.setRateOfInterest(existingApp.get().getRateOfInterest());
        application.setStatus(existingApp.get().getStatus());

        LoanApplication updated = loanApplicationService.updateLoanApplication(id, application);

        LoanApplicationResponse response = mapToResponse(updated);
        ApiResponse<LoanApplicationResponse> apiResponse =
                ApiResponse.success(
                        response,
                        "Loan application updated successfully"
                );

        logger.info("Loan application updated - ID: {}", id);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Deletes a loan application.
     *
     * @param id The application ID
     * @return ResponseEntity with confirmation message
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete loan application",
            description = "Deletes a loan application by its ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan application deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan application not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ApiResponse<Void>> deleteLoanApplication(
            @Parameter(description = "The ID of the loan application to delete")
            @PathVariable Long id) {

        logger.debug("Deleting loan application with ID: {}", id);

        try {
            loanApplicationService.deleteLoanApplication(id);

            ApiResponse<Void> apiResponse =
                    ApiResponse.success(
                            null,
                            "Loan application deleted successfully"
                    );

            logger.info("Loan application deleted - ID: {}", id);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<Void> apiResponse =
                    ApiResponse.error(
                            e.getMessage(),
                            "404"
                    );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * Maps LoanApplication entity to LoanApplicationResponse DTO.
     */
    private LoanApplicationResponse mapToResponse(LoanApplication application) {
        LoanApplicationResponse response = new LoanApplicationResponse();

        response.setApplicationId(application.getId());
        response.setFullName(application.getFullName());
        response.setPanNumber(application.getPanNumber());
        response.setLoanType(application.getLoanType());
        response.setMonthlyIncome(application.getMonthlyIncome());
        response.setRequestedAmount(application.getRequestedAmount());
        response.setTenureInMonths(application.getTenureInMonths());

        response.setCibilScore(application.getCibilScore());
        response.setEligibleAmount(application.getEligibleAmount());
        response.setRateOfInterest(application.getRateOfInterest());

        response.setStatus(application.getStatus());
        response.setSanctionedAmount(application.getSanctionedAmount());
        response.setProcessingFee(application.getProcessingFee());
        response.setMonthlyEmi(application.getMonthlyEmi());

        response.setPropertyValue(application.getPropertyValue());
        response.setPropertyAddress(application.getPropertyAddress());
        response.setIsSelfOccupied(application.getIsSelfOccupied());

        response.setCreatedAt(application.getCreatedAt());

        return response;
    }
}
