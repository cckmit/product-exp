package com.tmb.oneapp.productsexpservice.model.response.fundfactsheet;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundResponse {

    private boolean isError;

    private String errorCode;

    private String errorMsg;

    private String errorDesc;
}
