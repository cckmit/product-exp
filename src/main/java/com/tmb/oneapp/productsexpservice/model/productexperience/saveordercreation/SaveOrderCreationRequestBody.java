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

    private String orderId;

    private String effectiveDate;

    private String orderDateTime;

    private String workingHour;

    private String orderAmount;

    private OrderConfirmPayment paymentObject;
}
