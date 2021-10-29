package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationPaymentResponse {

    @JsonProperty(value = "orderID")
    private String orderId;

    private String orderDateTime;

    private String effectiveDate;

    private String workingHour;

    private String accountRedeem;

    private String frontEndFee;

    private String backEndFee;

    private String paymentDate;

    @JsonProperty(value = "paymentobj", access = JsonProperty.Access.WRITE_ONLY)
    private OrderConfirmPayment paymentObject;
}
