package com.tmb.oneapp.productsexpservice.model.response.fundffs;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FfsRsAndValidation {
    private FfsData body;
    private boolean isServiceClose;
    private boolean isUnderRiskLevel;
    private boolean isFundOfShelf;
    private boolean isNotBusinessOur;
    private boolean isCasaDormant;
    private boolean isError;
    private String errorCode;
    private String errorMsg;
    private String errorDesc;
}
