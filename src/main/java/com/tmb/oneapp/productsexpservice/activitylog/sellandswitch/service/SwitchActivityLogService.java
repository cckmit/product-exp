package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.enums.SwitchActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.request.SwitchActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The switch activity log service.
 */
@Service
public class SwitchActivityLogService {

    private final LogActivityService logActivityService;

    @Autowired
    public SwitchActivityLogService(LogActivityService logActivityService) {
        this.logActivityService = logActivityService;
    }

    /**
     * Generic Method to save activity log when enter pin is correct
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param ipAddress     the ip address
     * @param requestBody   the order creation payment request body
     * @param response      the order creation payment response
     * @return
     */
    @LogAround
    public void enterEnterPinIsCorrect(String correlationId, String crmId, String ipAddress,
                                       OrderCreationPaymentRequestBody requestBody,
                                       TmbOneServiceResponse<OrderCreationPaymentResponse> response) {

        SwitchActivityLog activityData = new SwitchActivityLog(correlationId, String.valueOf(System.currentTimeMillis()),
                SwitchActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        BaseEvent baseEvent = logActivityService.buildCommonData(crmId, ipAddress, response);

        activityData.setCrmId(baseEvent.getCrmId());
        activityData.setChannel(baseEvent.getChannel());
        activityData.setAppVersion(baseEvent.getAppVersion());
        activityData.setIpAddress(baseEvent.getIpAddress());
        activityData.setActivityStatus(baseEvent.getActivityStatus());
        activityData.setFailReason(baseEvent.getFailReason());

        activityData.setActivityType(SwitchActivityEnums.ENTER_PIN_IS_CORRECT.getEvent());

        activityData.setStatus(ProductsExpServiceConstant.SUCCESS.equalsIgnoreCase(baseEvent.getActivityStatus()) ?
                ActivityLogStatus.COMPLETED.getStatus() : ActivityLogStatus.FAILED.getStatus());
        activityData.setOrderId(response.getData() != null ? response.getData().getOrderId() : null);
        activityData.setUnitHolder(requestBody.getPortfolioNumber());
        activityData.setSourceFundName(requestBody.getFundCode());
        activityData.setSourceFundClassName(requestBody.getSourceFundClassName());
        activityData.setTargetFundName(requestBody.getSwitchFundCode());
        activityData.setTargetFundClassName(requestBody.getTargetFundClassName());

        if (ProductsExpServiceConstant.REVERSE_FLAG_Y.equalsIgnoreCase(requestBody.getFullRedemption())) {
            activityData.setTypeOfSwitching(ProductsExpServiceConstant.ACTIVITY_LOG_UNIT);
        } else {
            activityData.setTypeOfSwitching(UtilMap.getTypeOfTransaction(requestBody.getRedeemType().toLowerCase()));
        }

        if (ProductsExpServiceConstant.ACTIVITY_LOG_AMOUNT.equalsIgnoreCase(activityData.getTypeOfSwitching())) {
            activityData.setAmount(requestBody.getOrderAmount());
        } else {
            activityData.setAmount(requestBody.getOrderUnit());
        }

        logActivityService.createLog(activityData);
    }
}
