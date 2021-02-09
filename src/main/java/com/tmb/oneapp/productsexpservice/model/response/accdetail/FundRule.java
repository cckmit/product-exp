package com.tmb.oneapp.productsexpservice.model.response.accdetail;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundRule {
    private String fundHouseCode;
    private String fundCode;
    private String orderType;
    private String allotType;
    private String riskRate;
    private String tranStartDate;
    private String timeStart;
    private String tranEndDate;
    private String timeEnd;
    private String fundAllowOtx;
    private String trnAllowOtx;
    private String minInitAmount;
    private String maxInitAmount;
    private String minInitUnit;
    private String maxInitUnit;
    private String minAmount;
    private String maxAmount;
    private String minUnit;
    private String maxUnit;
    private String minHoldingAmount;
    private String minHoldingUnit;
    private String minHoldingCon;
    private String timeToRed;
    private String fxRisk;
    private String allowAipFlag;
    private String dateAfterIpo;
    private String frontEndFee;
    private String ipoFirstDate;
    private String ipoLastDate;
    private String ipoflag;
    private String secid;
    private String reserved1;
    private String reserved2;
    private String reserved3;
    private String reserved4;
    private String reserved5;
    private String termfundFlag;
    private String processFlag;
}
