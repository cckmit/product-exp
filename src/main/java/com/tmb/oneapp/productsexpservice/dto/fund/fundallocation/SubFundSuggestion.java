package com.tmb.oneapp.productsexpservice.dto.fund.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubFundSuggestion {
    private String fundCode;
    private String fundShortName;
    private String fundPercent;
}
