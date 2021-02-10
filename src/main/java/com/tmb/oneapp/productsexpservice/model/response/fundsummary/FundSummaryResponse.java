package com.tmb.oneapp.productsexpservice.model.response.fundsummary;

import com.tmb.oneapp.productsexpservice.model.portdata.Port;
import lombok.Data;

import java.util.List;

@Data
public class FundSummaryResponse {
    private com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse data;
    private List<Port> mutualFundAccounts;
}
