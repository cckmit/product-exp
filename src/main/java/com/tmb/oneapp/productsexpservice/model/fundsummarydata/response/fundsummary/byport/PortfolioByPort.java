package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport;

import lombok.Data;

import java.util.List;

@Data
public class PortfolioByPort {

    private String portfolioNumber;

    private String nickName;

    private String portPercent;

    private String jointFlag;

    private List<FundByPort> fundList;
}
