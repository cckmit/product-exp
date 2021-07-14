package com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Information {

    private String fundHouseCode;

    private String fundClassCode;

    private String thaiClassName;

    private String englishClassName;

    private String categoryName;

    private String masterName;

    private String inceptionDate;

    private String shareClassNetAssets;

    private String distributionStatus;

    private String switchingInFeePercent;

    private Actual actual;

    private String netExpenseRatio;

    private String minimumInitial;

    private String minimumSubsequent;

    private Asset asset;

    private EqSector eqSector;

    private String multiInvestmentStrategy;

    @JsonProperty(value = "exposureList")
    private List<Exposure> exposures;

    @JsonProperty(value = "holdingList")
    private List<Holding> holdings;

    private String riskSpectrumNumber;

    private String purchaseCutoffTime;

    private String redeemCutoffTime;

    private String switchingOutFeePercent;

    @JsonProperty(value = "return")
    private List<FundInformationTable> returnTable;

    private List<FundInformationTable> standardDeviation;

    private List<FundInformationTable> maxDrawdown;
}
