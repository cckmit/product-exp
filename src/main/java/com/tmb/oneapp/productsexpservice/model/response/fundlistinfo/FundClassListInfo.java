package com.tmb.oneapp.productsexpservice.model.response.fundlistinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundClassListInfo {
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
    private BigDecimal return1Day;
    private BigDecimal return1Month;
    private BigDecimal return3Month;
    private BigDecimal return6Month;
    private BigDecimal returnYTD;
    private BigDecimal return1Year;
    private BigDecimal return3Year;
    private BigDecimal return5Year;
    private BigDecimal inceptionSinceReturn;
    private String smartPortFlag;
    private String recommendFlag;
    private String followingFlag;
    private String boughtFlag;
    private String navAsOfDate;
    private String nav;
}



