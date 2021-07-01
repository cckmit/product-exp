package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FlexiLoanConfirmResponse {

        private SubmissionCustomerInfo customerInfo;
        private SubmissionPricingInfo pricingInfo;
        private String paymentMethod;
        private SubmissionPaymentInfo paymentInfo;
        private SubmissionReceivingInfo receivingInfo;
        private List<String> interestDetail;
}
