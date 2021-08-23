package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PercentOfFundType {

    private BigDecimal balanced;

    private BigDecimal foreignEquity;

    private BigDecimal foreignFixedIncome;

    private BigDecimal localEquity;

    private BigDecimal localFixedIncome;

    private String others;
}
