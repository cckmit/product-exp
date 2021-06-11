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
public class MutualFundWithFundSuggestedAllocation {
    public String fundClassCode;
    public String fundClassNameTh;
    public String fundClassNameEn;
    public String fundClassPercent;
    public String recommendedPercent;
    public List<FundSuggestion> fundSuggestionList;
}
