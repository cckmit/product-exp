package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum OpenPortfolioErrorEnums {
    NOT_IN_SERVICE_HOUR("2000001","Customer trade not in service available","error"),
    AGE_NOT_OVER_TWENTY("2000025","Age is not over 20 years old","error"),
    NO_ACTIVE_CASA_ACCOUNT("2000019","No active CASA account","error"),
    FAILED_VERIFY_KYC("2000022","Failed Verify KYC Flag Or ID Card has expired","error"),
    CUSTOMER_NOT_IN_LEVEL_FOUR("2000018","Customer is in risk level 4","error"),
    CUSTOMER_IN_RESTRICTED_LIST("2000018","Customer is in restricted list","error"),
    CUSTOMER_NOT_FILL_FATCA_FORM("2000034","Customer has not filled in the FATCA form","error"),
    ;

    private String code;
    private String msg;
    private String desc;

    OpenPortfolioErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
