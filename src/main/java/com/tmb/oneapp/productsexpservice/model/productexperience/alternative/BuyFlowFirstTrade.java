package com.tmb.oneapp.productsexpservice.model.productexperience.alternative;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyFlowFirstTrade {
    boolean isBuyFlow;
    boolean isFirstTrade;
}
