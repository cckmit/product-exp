package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum AlternativeOpenPortfolioErrorEnums {

    NOT_IN_SERVICE_HOUR("2000001", "Customer trade not in service available", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    AGE_NOT_OVER_TWENTY("2000025", "Age is not over 20 years old", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    NO_ACTIVE_CASA_ACCOUNT("2000019", "No active CASA account", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    FAILED_VERIFY_KYC("2000022", "Failed Verify KYC Flag Or ID Card has expired", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_IN_LEVEL_C3_AND_B3(AlternativeOpenPortfolioErrorEnums.ERROR_CODE_200018, "Customer is in risk level C3", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_IDENTIFY_ASSURANCE_LEVEL(AlternativeOpenPortfolioErrorEnums.ERROR_CODE_200018, "If IAL level >= 210 and IAL <> Null then allow", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED(AlternativeOpenPortfolioErrorEnums.ERROR_CODE_200018, "Customer has U.S. nationality or other 30 restricted nationalities. Including non nationality.", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    NOT_COMPLETED_FATCA_FORM("2000032", "Not completed the FATCA form", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    DID_NOT_PASS_FATCA_FORM("2000034", "Did not pass FATCA form. Please contact our ttb branch", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    CAN_NOT_OPEN_ACCOUNT_FOR_FATCA(AlternativeOpenPortfolioErrorEnums.ERROR_CODE_200018, "Can not open an investment account. Please contact our ttb branch", AlternativeOpenPortfolioErrorEnums.ERROR_DESC),
    ;

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";

    private String code;
    private String message;
    private String description;

    AlternativeOpenPortfolioErrorEnums(String code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
