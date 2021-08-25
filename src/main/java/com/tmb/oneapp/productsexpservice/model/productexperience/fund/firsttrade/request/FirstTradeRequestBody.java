package com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirstTradeRequestBody {

    @NotBlank
    private String portfolioNumber;

    @NotBlank
    private String fundCode;

}
