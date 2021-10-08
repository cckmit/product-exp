package com.tmb.oneapp.productsexpservice.model.productexperience.fund.processfirsttrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessFirstTradeRequestBody {

    private String portfolioNumber;

    private String fundHouseCode;

    private String fundCode;

    @JsonProperty(value = "orderID")
    private String orderId;

    private String effectiveDate;
}
