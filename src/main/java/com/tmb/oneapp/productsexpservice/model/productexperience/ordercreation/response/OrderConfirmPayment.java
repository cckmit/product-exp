package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response;

import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Card;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Fee;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Merchant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderConfirmPayment extends Payment {

    private String responseCodePayment;

    private String responseMsgPayment;

    private String currency;

    private AccountDetail account;

    private Card card;

    private Fee fee;

    private Merchant merchant;
}


