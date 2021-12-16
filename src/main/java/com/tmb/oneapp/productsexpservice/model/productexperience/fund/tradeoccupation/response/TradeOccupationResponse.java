package com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeOccupationResponse {

    private String firstTradeFlag;

    private String requirePosition;

    private String requireUpdate;

    private String occupationCode;

    private String occupationDescription;

    private String positionDescription;

}
