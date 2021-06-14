package com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation;

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
}