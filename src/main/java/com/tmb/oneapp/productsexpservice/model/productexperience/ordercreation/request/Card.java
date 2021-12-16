package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private String cardId;

    private String cardExpiry;

    private String cardEmbossingName;

    private String productId;

    private String productGroupId;
}
