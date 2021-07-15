package com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundList {
    private String fundCode;
    private String fundShortName;
    private String fundPercent;
}