package com.demo.loan_module.mapper;

import com.demo.loan_module.dto.LoanApplicationResponseDTO;
import com.demo.loan_module.entity.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "id", target = "applicationId")
    LoanApplicationResponseDTO toResponseDto(LoanApplication entity);

}