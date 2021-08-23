package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSummaryResponse {

    private FundSummaryHeader header;

    private FundSummaryBody body;
}
