package com.tmb.oneapp.productsexpservice.model.response.fundsummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundSummaryByPortResponse {
    private FundSummaryByPortBody body;
}
