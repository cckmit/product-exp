package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service;

import com.tmb.common.logger.LogAround;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.enums.SellActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.request.SellActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The sell activity log service.
 */
@Service
public class SellActivityLogService {


    private final LogActivityService logActivity;

    @Autowired
    public SellActivityLogService(LogActivityService logActivity) {
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

        SellActivityLog activityData = new SellActivityLog(
                correlationId, String.valueOf(System.currentTimeMillis()),
                SellActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        activityData.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        activityData.setActivityStatus(status);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        activityData.setFailReason("");

        activityData.setStatus(status);
        activityData.setOrderId(paymentResponseBody != null ? paymentResponseBody.getOrderId() : null);
        activityData.setUnitHolder(paymentRequestBody.getPortfolioNumber());
        activityData.setFundName(paymentRequestBody.getFundName());
        activityData.setFundClass(!StringUtils.isEmpty(paymentRequestBody.getFundThaiClassName()) ? paymentRequestBody.getFundThaiClassName() : paymentRequestBody.getFundEnglishClassName());

        if (ProductsExpServiceConstant.REVERSE_FLAG_Y.equalsIgnoreCase(paymentRequestBody.getFullRedemption())) {
            activityData.setTypeOfSelling(ProductsExpServiceConstant.ACTIVITY_LOG_UNIT);
        } else {
            activityData.setTypeOfSelling(UtilMap.getTypeOfTransaction(paymentRequestBody.getRedeemType().toLowerCase()));
        }

        if (ProductsExpServiceConstant.ACTIVITY_LOG_AMOUNT.equalsIgnoreCase(activityData.getTypeOfSelling())) {
            activityData.setAmount(paymentRequestBody.getOrderAmount());
        } else {
            activityData.setAmount(paymentRequestBody.getOrderUnit());
        }

        activityData.setReceivingAccount(paymentResponseBody != null ? paymentResponseBody.getAccountRedeem() : null);
        activityData.setActivityType(SellActivityEnums.ENTER_PIN_IS_CORRECT.getEvent());
        logActivity.createLog(activityData);
    }
}
