package com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeOccupationResponse {

    @NotBlank
    private String firstTradeFlag;

    @NotBlank
    private String requirePosition;

}
