package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FundSummaryBody {
    private FundClassList fundClassList;
    private String feeAsOfDate;
    private BigDecimal sumAccruedFee;
    private PercentOfFundType percentOfFundType;
    private List<String> portsUnitHolder;

}
