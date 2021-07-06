package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static void throwTmbException(String message) throws TMBCommonException {
        throw new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                message,
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);
    }
}
