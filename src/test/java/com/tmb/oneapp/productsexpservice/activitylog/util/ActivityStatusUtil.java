package com.tmb.oneapp.productsexpservice.activitylog.util;

import com.tmb.common.model.BaseEvent;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.util.UtilMap;

public class ActivityStatusUtil {

    public static BaseEvent buildSuccessStatus(String crmId, String ipAddress) {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        baseEvent.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        baseEvent.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        baseEvent.setIpAddress(ipAddress);
        baseEvent.setActivityStatus(ActivityLogStatus.SUCCESS.getStatus());
        return baseEvent;
    }

    public static BaseEvent buildFailedStatus(String crmId, String ipAddress) {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        baseEvent.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        baseEvent.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        baseEvent.setIpAddress(ipAddress);
        baseEvent.setActivityStatus(ActivityLogStatus.FAILURE.getStatus());
        baseEvent.setFailReason("");
        return baseEvent;
    }
}
