package com.tmb.oneapp.productsexpservice.mapper.dca;

import com.tmb.oneapp.productsexpservice.model.productexperience.aip.request.AipValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.request.TransactionValidationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DcaMapper {

    @Mapping(target = "crmId", ignore = true)
    TransactionValidationRequest dcaValidationRequestToTransactionValidationRequest(DcaValidationRequest dcaValidationRequest);

    AipValidationRequest dcaValidationRequestToAipValidationRequest(DcaValidationRequest dcaValidationRequest);
}
