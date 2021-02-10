package com.tmb.oneapp.productsexpservice.model.response.fundsummary;

import com.tmb.common.model.Status;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.FundSummaryResponseData;
import com.tmb.oneapp.productsexpservice.model.portdata.Port;
import lombok.Data;

import java.util.List;

@Data
public class FundSummaryResponse {
    private FundSummaryResponseData data;
    private List<Port> mutualFundAccounts;
}
