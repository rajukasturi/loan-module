package com.demo.loan_module.mapper;

import com.demo.loan_module.dto.LoanApplicationResponseDTO;
import com.demo.loan_module.dto.request.CreateLoanApplicationRequest;
import com.demo.loan_module.dto.response.LoanApplicationResponse;
import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.model.EligibilityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * MapStruct mapper interface for loan application DTOs.
 * Provides type-safe mapping between entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface LoanMapper {
    
    /**
     * Maps LoanApplication entity to LoanApplicationResponseDTO.
     */
    @Mappings({
        @Mapping(source = "id", target = "applicationId"),
        @Mapping(source = "status", target = "status"),
        @Mapping(source = "createdAt", target = "createdAt")
    })
    LoanApplicationResponseDTO toResponseDto(LoanApplication entity);
    
    /**
     * Maps LoanApplication entity to LoanApplicationResponse.
     */
    @Mappings({
        @Mapping(source = "id", target = "applicationId"),
        @Mapping(source = "status", target = "status")
    })
    LoanApplicationResponse toLoanApplicationResponse(LoanApplication entity);
    
    /**
     * Maps CreateLoanApplicationRequest to LoanApplication entity.
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "cibilScore", ignore = true),
        @Mapping(target = "eligibleAmount", ignore = true),
        @Mapping(target = "rateOfInterest", ignore = true),
        @Mapping(target = "propertyValue", source = "propertyDetails.propertyValue"),
        @Mapping(target = "propertyAddress", source = "propertyDetails.propertyAddress"),
        @Mapping(target = "isSelfOccupied", source = "propertyDetails.isSelfOccupied"),
        @Mapping(target = "employmentType", source = "employmentDetails.employmentType"),
        @Mapping(target = "companyName", source = "employmentDetails.companyName"),
        @Mapping(target = "yearsOfService", source = "employmentDetails.yearsOfService")
    })
    LoanApplication toEntity(CreateLoanApplicationRequest request);
    
    /**
     * Maps EligibilityResponse to LoanApplication (partial update).
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "fullName", ignore = true),
        @Mapping(target = "panNumber", ignore = true),
        @Mapping(target = "monthlyIncome", ignore = true),
        @Mapping(target = "requestedAmount", ignore = true),
        @Mapping(target = "status", ignore = true)
    })
    void updateEntityFromEligibility(EligibilityResponse eligibility, @MappingTarget LoanApplication entity);
}
