package com.demo.loan_module.controller;

import com.demo.loan_module.dto.LoanApplicationResponseDTO;
import com.demo.loan_module.model.LoanRequest;
import com.demo.loan_module.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    private final LoanService loanService;

    public LoanApplicationController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public ResponseEntity<LoanApplicationResponseDTO> applyForLoan(@RequestBody LoanRequest request) {
        LoanApplicationResponseDTO savedApplication = loanService.processLoanApplication(request);
        return ResponseEntity.ok(savedApplication);
    }
}