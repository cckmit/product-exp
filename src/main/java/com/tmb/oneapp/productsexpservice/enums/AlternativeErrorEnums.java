package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum AlternativeErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001","Customer trade not in service available", AlternativeErrorEnums.ERROR_DESC),
    AGE_NOT_OVER_TWENTY("2000025","Age is not over 20 years old", AlternativeErrorEnums.ERROR_DESC),
    NO_ACTIVE_CASA_ACCOUNT("2000019","No active CASA account", AlternativeErrorEnums.ERROR_DESC),
    FAILED_VERIFY_KYC("2000022","Failed Verify KYC Flag Or ID Card has expired", AlternativeErrorEnums.ERROR_DESC),
    CUSTOMER_IN_LEVEL_C3_AND_B3(AlternativeErrorEnums.ERROR_CODE_200018,"Customer is in risk level C3 ,B3", AlternativeErrorEnums.ERROR_DESC),
    CUSTOMER_IDENTIFY_ASSURANCE_LEVEL(AlternativeErrorEnums.ERROR_CODE_200018,"If IAL level >= 210 and IAL <> Null then allow", AlternativeErrorEnums.ERROR_DESC),
    CUSTOMER_IN_RESTRICTED_LIST(AlternativeErrorEnums.ERROR_CODE_200018,"Customer is in restricted list", AlternativeErrorEnums.ERROR_DESC),
    CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED(AlternativeErrorEnums.ERROR_CODE_200018,"Customer has U.S. nationality or other 30 restricted nationalities. Including non nationality.", AlternativeErrorEnums.ERROR_DESC),
    CUSTOMER_NOT_FILL_FATCA_FORM("2000034","Customer has not filled in the FATCA form", AlternativeErrorEnums.ERROR_DESC),
    ;

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";
    private String code;
    private String msg;
    private String desc;

    AlternativeErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
