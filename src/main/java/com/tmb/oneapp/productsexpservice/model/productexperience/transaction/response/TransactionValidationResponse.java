package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionValidationResponse {

    private Status status;

    private TransactionValidationResponseBody data;
}
