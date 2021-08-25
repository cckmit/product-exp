package com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirstTradeResponseBody {

    private String portfolioNumber;

    private String fundCode;

    private String firstTradeFlag;

}
