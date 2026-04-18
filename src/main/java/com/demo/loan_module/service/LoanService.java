package com.demo.loan_module.service;

import com.demo.loan_module.dto.LoanApplicationResponseDTO;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.mapper.LoanMapper;
import com.demo.loan_module.model.EligibilityResponse;
import com.demo.loan_module.model.LoanRequest;
import com.demo.loan_module.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final MockCibilService cibilService;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository, MockCibilService cibilService, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.cibilService = cibilService;
        this.loanMapper = loanMapper;
    }

    public LoanApplicationResponseDTO processLoanApplication(LoanRequest request) {
        // 1. Call Mock API
        EligibilityResponse eligibility =
                cibilService.checkEligibility(request.getPanNumber(), request.getMonthlyIncome());

        // 2. Map to Entity
        LoanApplication application = new LoanApplication();
        application.setFullName(request.getFullName());
        application.setPanNumber(request.getPanNumber());
        application.setMonthlyIncome(request.getMonthlyIncome());
        application.setRequestedAmount(request.getRequestedAmount());
        application.setCibilScore(eligibility.getCibilScore());
        application.setEligibleAmount(eligibility.getEligibleAmount());
        application.setRateOfInterest(eligibility.getRateOfInterest());

        // 3. Determine Status
        if (eligibility.isEligible() && request.getRequestedAmount() <= eligibility.getEligibleAmount()) {
            application.setStatus("APPROVED");
        } else {
            application.setStatus("REJECTED");
        }

        // 4. Save to H2 Database and return
        LoanApplication savedApplication = loanRepository.save(application);

        // 5. Use MapStruct to return the DTO
        return loanMapper.toResponseDto(savedApplication);
    }
}
