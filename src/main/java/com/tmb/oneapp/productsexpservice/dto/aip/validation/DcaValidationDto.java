package com.tmb.oneapp.productsexpservice.dto.aip.validation;

import com.tmb.oneapp.productsexpservice.model.productexperience.aip.response.AipValidationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response.TransactionValidationResponseBody;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DcaValidationDto {

    private TransactionValidationResponseBody transactionValidation;

    private AipValidationResponseBody aipValidation;
}
