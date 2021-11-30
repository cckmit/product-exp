package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum AlternativeBuySellSwitchDcaErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001", "Customer trade not in service available", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    AGE_NOT_OVER_TWENTY("2000042", "Age is not over 20 years old", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_IN_LEVEL_C3_AND_B3("2000002", "Customer is in risk level C3 or (B3 on buy flow first trade)", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CUSTOMER_SUIT_EXPIRED("2000026", "Customer Suitability Expired", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CASA_DORMANT("2000003", "Can not do the transaction because you account is not found or account status is not ready for any transaction.", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    CAN_NOT_BUY_FUND("2000006", "Cannot buy the fund", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    ID_CARD_EXPIRED("2000022", "Your ID card has expired. Can not do the transaction.", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    NO_ACCOUNT_REDEMPTION("2000013", "Cannot proceed because your receiving account is not found. Please contact ttb branch.", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC),
    FUND_OFF_SHELF("2000005", "This fund is not allow to purchase.", AlternativeBuySellSwitchDcaErrorEnums.ERROR_DESC);

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";

    private String code;
    private String message;
    private String description;

    AlternativeBuySellSwitchDcaErrorEnums(String code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
