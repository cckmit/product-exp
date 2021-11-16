package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAIPResponseBody {

    private String portfolioNumber;

    private String orderID;

    private String orderDateTime;

    private String effectiveDate;

    private String orderAmount;

    private String bankAccountNumber;

}
