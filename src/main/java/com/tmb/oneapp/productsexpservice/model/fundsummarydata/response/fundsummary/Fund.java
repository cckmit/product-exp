package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Fund {
    private String portfolioNumber;
    private String fundCode;
    private String fundNameEN;
    private String fundNameTH;
    private String fundNickNameEN;
    private String fundNickNameTH;
    private String fundShortName;
    private BigDecimal unrealizedProfit;
    private BigDecimal marketValue;
    private BigDecimal unrealizedProfitPercent;
    private BigDecimal accruedFee;

}
