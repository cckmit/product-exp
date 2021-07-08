package com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSuggestAllocationList {
    private String fundClassCode;
    private String fundClassNameTh;
    private String fundClassNameEn;
    private String recommendedPercent;
    private List<FundList> fundList;
}
