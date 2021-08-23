package com.tmb.oneapp.productsexpservice.model.response.investment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundDetail {

    private String portfolioNumber;

    private String fundHouseCode;

    private String englishFundName;

    private String thaiFundName;

    private String dateAsOf;

    private String unit;

    private String nav;

    private String cost;

    private String investmentValue;

    private String unrealizedProfit;

    private String unrealizedProfitPerc;

    private String taxDoc;

    private String unitLTFFiveYear;

    private String unitSmartPort;

    private String fundType;

    private String averageCost;

    private String nickname;

    private String unitHolderNumber;

    private String jointFlag;
}
