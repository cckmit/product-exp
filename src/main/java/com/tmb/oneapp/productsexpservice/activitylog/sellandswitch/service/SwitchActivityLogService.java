package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service;

import com.tmb.common.logger.LogAround;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.enums.SwitchActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.request.SwitchActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
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

    private final LogActivityService logActivity;

    @Autowired
    public SwitchActivityLogService(LogActivityService logActivity) {
        this.logActivity = logActivity;
    }

    /**
     * Generic Method to save activity log when enter pin is correct
     *
     * @param correlationId       the correlation id
     * @param crmId               the crm id
     * @param paymentRequestBody  the order creation payment request body
     * @param paymentResponseBody the order creation payment response body
     * @return
     */
    @LogAround
    public void enterEnterPinIsCorrect(String correlationId, String crmId, String status,
                                       OrderCreationPaymentRequestBody paymentRequestBody,
                                       OrderCreationPaymentResponse paymentResponseBody) {

        SwitchActivityLog activityData = new SwitchActivityLog(correlationId, String.valueOf(System.currentTimeMillis()),
                SwitchActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        activityData.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        activityData.setActivityStatus(status);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        activityData.setFailReason("");

        activityData.setStatus(status);
        activityData.setOrderId(paymentResponseBody != null ? paymentResponseBody.getOrderId() : null);
        activityData.setUnitHolder(paymentRequestBody.getPortfolioNumber());
        activityData.setSourceFundName(paymentRequestBody.getFundCode());
        activityData.setSourceFundClassName(paymentRequestBody.getSourceFundClassName());
        activityData.setTargetFundName(paymentRequestBody.getSwitchFundCode());
        activityData.setTargetFundClassName(paymentRequestBody.getTargetFundClassName());

        if (ProductsExpServiceConstant.REVERSE_FLAG_Y.equalsIgnoreCase(paymentRequestBody.getFullRedemption())) {
            activityData.setTypeOfSwitching(ProductsExpServiceConstant.ACTIVITY_LOG_UNIT);
        } else {
            activityData.setTypeOfSwitching(UtilMap.getTypeOfTransaction(paymentRequestBody.getRedeemType().toLowerCase()));
        }

        if (ProductsExpServiceConstant.ACTIVITY_LOG_AMOUNT.equalsIgnoreCase(activityData.getTypeOfSwitching())) {
            activityData.setAmount(paymentRequestBody.getOrderAmount());
        } else {
            activityData.setAmount(paymentRequestBody.getOrderUnit());
        }
        activityData.setActivityType(SwitchActivityEnums.ENTER_PIN_IS_CORRECT.getEvent());
        logActivity.createLog(activityData);
    }
}
