package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import org.springframework.http.HttpStatus;

public class TmbErrorHandle {

    protected void tmbResponseErrorHandle(TmbStatus tmbStatus) throws TMBCommonException{
        if(!ProductsExpServiceConstant.SUCCESS_CODE.equals(tmbStatus.getCode())){
            throw new TMBCommonException(
                    tmbStatus.getCode(),
                    tmbStatus.getMessage(),
                    tmbStatus.getService(),
                    HttpStatus.BAD_REQUEST,
                    null);
        }
    }
}
