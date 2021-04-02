package com.tmb.oneapp.productsexpservice.model.response.ncb;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NcbPaymentConfirmResponse {
    private String transactionDate;
    private String referenceNo;
}
