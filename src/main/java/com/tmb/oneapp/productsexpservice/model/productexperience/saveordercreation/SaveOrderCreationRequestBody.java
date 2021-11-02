package com.tmb.oneapp.productsexpservice.model.productexperience.saveordercreation;

import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderConfirmPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveOrderCreationRequestBody {

    private String portfolioNumber;

    private String orderAmount;

    private String orderId;

    private OrderConfirmPayment paymentObject;
}
