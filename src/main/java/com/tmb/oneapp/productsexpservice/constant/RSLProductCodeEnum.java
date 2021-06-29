package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * enum class for response code to maintain response status
 */
@Getter
@AllArgsConstructor
public enum RSLProductCodeEnum implements Serializable {
    MO("MO", "Mortgage", "สินเชื่อบ้าน"),
    PL("PL", "Personal loan", "สินเชื่อบุคคล"),
    CC("CC", "Credit Card", "บัตรเครดิต"),
    SM("SM", "Staff Mortgage", "สินเชื่อบ้านสำหรับพนักงาน"),
    SP("SP", "Staff Personal loan", "สินเชื่อบุคคลสำหรับพนักงาน"),
    CREDIT_CARD_TTB_ABSOLUTE("VJ", "ttb absolute", "บัตร ทีทีบี แอป โซลูท"),
    CREDIT_CARD_TTB_SO_FAST("VP", "ttb so fast", "บัตร ทีทีบี โซ ฟาสต์"),
    CREDIT_CARD_TTB_SO_SMART("VM", "ttb so smart", "บัตร ทีทีบี โซ สมาร์ท"),
    CREDIT_CARD_TTB_SO_CHILL("VH", "ttb so chill", "บัตร ทีทีบี โซ ชิลล์"),
    CREDIT_CARD_TTB_RESERVE_INFINITE("VI", "ttb reserve infinite", "บัตร ทีทีบี รีเซิร์ฟ อินฟินิท"),
    CREDIT_CARD_TTB_RESERVE_SIGNATURE("VB", "ttb reserve signature", "บัตร ทีทีบี รีเซิร์ฟ ซิกเนเจอร์"),
    FLASH_CARD_PLUS("RC01", "ttb flash plus", "บัตร ทีทีบี แฟลช พลัส"),
    CASH_2_GO("C2G01", "Cash 2 Go", "แคชทูโก"),
    CASH_2_GO_TOPUP("C2G02", "Cash 2 Go", "แคชทูโก");

    private final String productCode;
    private final String productNameEn;
    private final String productNameTh;

}
