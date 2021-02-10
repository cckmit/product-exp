package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundSummaryBody {
    private FundClassList fundClassList;
    private String feeAsOfDate;
    private BigDecimal sumAccruedFee;
    private PercentOfFundType percentOfFundType;
}
