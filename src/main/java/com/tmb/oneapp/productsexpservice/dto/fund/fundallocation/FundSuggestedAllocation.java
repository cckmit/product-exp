package com.tmb.oneapp.productsexpservice.dto.fund.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundSuggestedAllocation {
    private String suitabilityScore;
    private String modelNumber;
    private List<FundSuggestion> fundSuggestionList;
}
