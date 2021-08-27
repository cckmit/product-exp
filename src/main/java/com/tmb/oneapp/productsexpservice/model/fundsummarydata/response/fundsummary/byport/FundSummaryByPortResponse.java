package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundSummaryByPortResponse {

    private FundSummaryByPortBody body;
}
