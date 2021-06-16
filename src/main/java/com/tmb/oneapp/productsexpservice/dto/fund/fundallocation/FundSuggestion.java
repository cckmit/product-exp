package com.tmb.oneapp.productsexpservice.dto.fund.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundSuggestion {
    private String fundClassCode;
    private String fundClassNameTh;
    private String fundClassNameEn;
    private String recommendedPercent;
}
