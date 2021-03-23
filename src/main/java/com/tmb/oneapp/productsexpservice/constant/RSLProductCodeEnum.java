package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

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
    SP("SP", "Staff Personal loan", "สินเชื่อบุคคลสำหรับพนักงาน");

    private final String productCode;
    private final String productNameEn;
    private final String productNameTh;

}