package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum OpenPortfolioErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001","Customer trade not in service available",OpenPortfolioErrorEnums.ERROR_DESC),
    AGE_NOT_OVER_TWENTY("2000025","Age is not over 20 years old",OpenPortfolioErrorEnums.ERROR_DESC),
    NO_ACTIVE_CASA_ACCOUNT("2000019","No active CASA account",OpenPortfolioErrorEnums.ERROR_DESC),
    FAILED_VERIFY_KYC("2000022","Failed Verify KYC Flag Or ID Card has expired",OpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_IN_LEVEL_C3_AND_B3(OpenPortfolioErrorEnums.ERROR_CODE_200018,"Customer is in risk level C3 ,B3",OpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_IDENTIFY_ASSURANCE_LEVEL(OpenPortfolioErrorEnums.ERROR_CODE_200018,"If IAL level >= 210 and IAL <> Null then allow",OpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_IN_RESTRICTED_LIST(OpenPortfolioErrorEnums.ERROR_CODE_200018,"Customer is in restricted list",OpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED(OpenPortfolioErrorEnums.ERROR_CODE_200018,"Customer has U.S. nationality or other 30 restricted nationalities. Including non nationality.",OpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_NOT_FILL_FATCA_FORM("2000034","Customer has not filled in the FATCA form",OpenPortfolioErrorEnums.ERROR_DESC),
    ;

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";
    private String code;
    private String msg;
    private String desc;

    OpenPortfolioErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
