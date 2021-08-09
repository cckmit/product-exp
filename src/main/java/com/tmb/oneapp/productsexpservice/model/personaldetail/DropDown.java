package com.tmb.oneapp.productsexpservice.model.personaldetail;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DropDown {
    private String entryCode;
    private BigDecimal entryId;
    private String entryNameEng;
    private String entryNameTh;
    private String entrySource;
}
