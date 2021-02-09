package com.tmb.oneapp.productsexpservice.model.response.investmentrs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailFund {
    private String unitHolderNo;
    private String fundHouseCode;
    private String fundNameEN;
    private String fundNameTH;
    private String dateAsOf;
    private String unit;
    private String nav;
    private String cost;
    private String investmentValue;
    private String unrealizedProfit;
    private String unrealizedProfitPerc;
    private String taxDoc;
    private String unitLTF5Y;
}
