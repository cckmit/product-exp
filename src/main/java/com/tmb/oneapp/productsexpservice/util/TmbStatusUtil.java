package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;

public class TmbStatusUtil {

    private TmbStatusUtil() {
    }

    public static TmbStatus successStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.SUCCESS_CODE);
        status.setDescription(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }

    public static TmbStatus notFoundStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
        status.setDescription(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }
}
