package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum OpenPortfolioErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001","Customer trade not in service available",OpenPortfolioErrorEnums.errorDesc),
    AGE_NOT_OVER_TWENTY("2000025","Age is not over 20 years old",OpenPortfolioErrorEnums.errorDesc),
    NO_ACTIVE_CASA_ACCOUNT("2000019","No active CASA account",OpenPortfolioErrorEnums.errorDesc),
    FAILED_VERIFY_KYC("2000022","Failed Verify KYC Flag Or ID Card has expired",OpenPortfolioErrorEnums.errorDesc),
    CUSTOMER_IN_LEVEL_C3_AND_B3(OpenPortfolioErrorEnums.errorCode200018,"Customer is in risk level C3 ,B3",OpenPortfolioErrorEnums.errorDesc),
    CUSTOMER_IDENTIFY_ASSURANCE_LEVEL(OpenPortfolioErrorEnums.errorCode200018,"If IAL level >= 210 and IAL <> Null then allow",OpenPortfolioErrorEnums.errorDesc),
    CUSTOMER_IN_RESTRICTED_LIST(OpenPortfolioErrorEnums.errorCode200018,"Customer is in restricted list",OpenPortfolioErrorEnums.errorDesc),
    CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED(OpenPortfolioErrorEnums.errorCode200018,"Customer has U.S. nationality or other 30 restricted nationalities. Including non nationality.",OpenPortfolioErrorEnums.errorDesc),
    CUSTOMER_NOT_FILL_FATCA_FORM("2000034","Customer has not filled in the FATCA form",OpenPortfolioErrorEnums.errorDesc),
    ;

    private static final String errorDesc = "error";
    private static final String errorCode200018 = "2000018";
    private String code;
    private String msg;
    private String desc;

    OpenPortfolioErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
