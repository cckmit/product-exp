package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport;

import lombok.Data;

import java.util.List;

@Data

public class FundSummaryByPortBody {

    private List<PortfolioByPort> portfolioList;
}
