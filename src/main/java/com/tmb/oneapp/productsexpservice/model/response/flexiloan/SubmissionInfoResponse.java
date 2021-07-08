package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubmissionInfoResponse {
    private SubmissionCustomerInfo customerInfo;
    private SubmissionPricingInfo pricingInfo;
    private String paymentMethod;
    private SubmissionPaymentInfo submissionInfo;
    private SubmissionReceivingInfo receivingInfo;
    private BigDecimal tenure;
}
