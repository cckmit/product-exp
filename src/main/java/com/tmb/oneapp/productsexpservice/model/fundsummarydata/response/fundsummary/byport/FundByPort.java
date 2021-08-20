package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport;

import lombok.Data;

@Data
public class FundByPort {

    private String fundHouseCode;

    private String fundCode;

    private String fundShortName;

    private String unrealizedProfit;

    private String marketValue;

    private String unrealizedProfitPercent;

    private String allotType;
}
