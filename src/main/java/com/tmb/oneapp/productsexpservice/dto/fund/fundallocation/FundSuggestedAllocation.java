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
public class FundSuggestedAllocation {
    public String suitabilityScore;
    public String modelNumber;
    public List<FundSuggestion> fundSuggestionList;
}
