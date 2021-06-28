package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum FatcaErrorEnums {
    CUSTOMER_NOT_FILLED_IN("2000032","Customer has not filled in the Fatca form","error"),
    USNATIONAL("2000018","Customer has us","error");

    private String code;
    private String msg;
    private String desc;

    FatcaErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
