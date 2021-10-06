package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.enums;

import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import lombok.Getter;

@Getter
public enum SellActivityEnums {

    REDEEM_BUTTON_AT_FILLING_SCREEN("101000301", ProductsExpServiceConstant.INVESTMENT_SELL_FUND_TYPE, "Click redeem button on filling in screen "),
    ACCEPT_TERM_AND_CONDITION("101000302", ProductsExpServiceConstant.INVESTMENT_SELL_FUND_TYPE, "Accept T&C, Risk and click confirm button"),
    ENTER_PIN_IS_CORRECT("101000303", ProductsExpServiceConstant.INVESTMENT_SELL_FUND_TYPE, "Enter PIN is correct then display transaction details completed");

    private String activityTypeId;
    private String description;
    private String event;

    SellActivityEnums(String activityTypeId, String description, String event) {
        this.activityTypeId = activityTypeId;
        this.description = description;
        this.event = event;
    }
}
