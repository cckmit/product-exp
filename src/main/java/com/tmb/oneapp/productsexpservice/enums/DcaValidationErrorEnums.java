package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum DcaValidationErrorEnums {

    PTES_PORT_IS_NOT_ALLOW_FOR_DCA("2000036","This account can be transacted via Eastspring channel only.", DcaValidationErrorEnums.ERROR_DESC),
    FUND_NOT_ALLOW_SET_DCA("2000037","This fund is not allowed to set DCA plan.", DcaValidationErrorEnums.ERROR_DESC),
    ;

    private static final String ERROR_DESC = "error";
    private static final String ERROR_CODE_200018 = "2000018";
    private String code;
    private String msg;
    private String desc;

    DcaValidationErrorEnums(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }
}
