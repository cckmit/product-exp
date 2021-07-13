package com.tmb.oneapp.productsexpservice.model.response.investment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundDetail {

    @JsonProperty(value = "unitHolderNo")
    private String unitHolderNumber;

    private String fundHouseCode;

    @JsonProperty(value = "fundNameEN")
    private String fundEnglishName;

    @JsonProperty(value = "fundNameTH")
    private String fundThaiName;

    private String dateAsOf;

    private String unit;

    private String nav;

    private String cost;

    private String investmentValue;

    private String unrealizedProfit;

    private String unrealizedProfitPerc;

    private String taxDoc;

    private String unitLTF5Y;

    @JsonProperty(value = "nickName")
    private String nickname;
}
