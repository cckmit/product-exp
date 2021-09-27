package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class TmbErrorHandle {
    protected void errorHandle() throws TMBCommonException {
        throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }
}
