package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSummaryResponse {
    private FundSummaryBody body;
    private FundSummaryHeader header;
}
