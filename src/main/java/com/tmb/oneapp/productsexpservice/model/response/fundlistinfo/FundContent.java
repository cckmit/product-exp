package com.tmb.oneapp.productsexpservice.model.response.fundlistinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundContent {
    private String fundHouseCode;
    private String fundCode;
    private String fundShortName;
    private String fundNameEn;
    private String fundNameTh;
    private String riskRate;
    private String allotType;
    @JsonProperty("fundclassCode")
    private String fundClassCode;
    private String fundTypeL1;
    private String fundTypeL2;
    @JsonProperty("secid")
    private String secId;
    private String reserved1;
    private String reserved2;
    private String reserved3;
    private String reserved4;
    private String reserved5;
    private String fxRisk;
    private String allowAipFlag;
    private String ipoDateFrom;
    private String ipoDateTo;
    private String dateAfterIpo;
    private String ipoFlag;
    @JsonProperty("fundclassCodeHub")
    private String fundClassCodeHub;
    @JsonProperty("fundclassNameEnHub")
    private String fundClassNameEnHub;
    @JsonProperty("fundclassNameThHub")
    private String fundClassNameThHub;
    @JsonProperty("termfundFlag")
    private String termFundFlag;
    private String inceptionDate;
    private String return1Day;
    private String return1Month;
    private String return3Month;
    private String return6Month;
    private String returnYTD;
    private String return1Year;
    private String return3Year;
    private String return5Year;
    private String inceptionSinceReturn;
}

