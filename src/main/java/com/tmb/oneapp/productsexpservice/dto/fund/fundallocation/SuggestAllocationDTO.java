package com.tmb.oneapp.productsexpservice.dto.fund.fundallocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuggestAllocationDTO {
    private List<MutualFund> mutualFund;
    private FundSuggestedAllocation fundSuggestedAllocation;
    private List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocation;
}
