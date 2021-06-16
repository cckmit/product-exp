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
public class MutualFundWithFundSuggestedAllocation {
    private String fundClassCode;
    private String fundClassNameTh;
    private String fundClassNameEn;
    private String fundClassPercent;
    private String recommendedPercent;
    private List<SubFundSuggestion> fundSuggestionList;
}
