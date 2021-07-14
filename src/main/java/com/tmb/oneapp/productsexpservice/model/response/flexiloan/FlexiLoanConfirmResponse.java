package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FlexiLoanConfirmResponse {

        private SubmissionCustomerInfo customerInfo;
        private SubmissionPricingInfo pricingInfo;
        private SubmissionPaymentInfo paymentInfo;
        private SubmissionReceivingInfo receivingInfo;
}
