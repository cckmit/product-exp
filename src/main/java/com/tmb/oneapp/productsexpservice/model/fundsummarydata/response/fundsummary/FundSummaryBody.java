package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.PortfolioByPort;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundSummaryBody extends FundClassList {

    private FundClassList fundClassList;

    private String feeAsOfDate;

    private BigDecimal sumAccruedFee;

    private PercentOfFundType percentOfFundType;

    private List<String> portsUnitHolder;

    private List<FundSearch> searchList;

    private List<PortfolioByPort> summaryByPort;

    private String unrealizedProfitPercent;

    private String summaryMarketValue;

    private String summaryUnrealizedProfit;

    private String summaryUnrealizedProfitPercent;

    private String summarySmartPortMarketValue;

    private String summarySmartPortUnrealizedProfit;

    private String summarySmartPortUnrealizedProfitPercent;

    private Boolean isPt;

    private Boolean isPtes;

    private Boolean isSmartPort;

    private List<FundClass> smartPortList;

    private List<FundClass> ptPortList;

    private String countProcessedOrder;

    private Boolean isJointPortOnly;
}
