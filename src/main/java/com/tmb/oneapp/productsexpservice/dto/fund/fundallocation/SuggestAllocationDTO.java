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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestAllocationDTO {
    public List<MutualFund> mutualFund;
    public FundSuggestedAllocation fundSuggestedAllocation;
    public List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocation;
}
