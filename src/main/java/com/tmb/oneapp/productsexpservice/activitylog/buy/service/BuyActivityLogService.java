package com.tmb.oneapp.productsexpservice.activitylog.buy.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.buy.enums.BuyActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.buy.request.BuyActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The buy activity log service.
 */
@Service
public class BuyActivityLogService {

    private final LogActivityService logActivityService;

    @Autowired
    public BuyActivityLogService(LogActivityService logActivityService) {
        this.logActivityService = logActivityService;
    }

    /**
     * Generic Method to save activity log when click purchase button at fund fact sheet screen
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param ipAddress             the ip address
     * @param alternativeBuyRequest the alternative buy request
     * @param tmbOneServiceResponse the TMB OneApp response
     * @return
     */
    @LogAround
    public void clickPurchaseButtonAtFundFactSheetScreen(String correlationId, String crmId, String ipAddress,
                                                         AlternativeBuyRequest alternativeBuyRequest,
                                                         TmbOneServiceResponse<String> tmbOneServiceResponse) {
        BuyActivityLog activityData = new BuyActivityLog(
                correlationId, String.valueOf(System.currentTimeMillis()),
                BuyActivityEnums.CLICK_PURCHASE_BUTTON_AT_FUND_FACT_SHEET_SCREEN.getActivityTypeId());
        BaseEvent baseEvent = logActivityService.buildCommonData(crmId, ipAddress, tmbOneServiceResponse);

        activityData.setCrmId(baseEvent.getCrmId());
        activityData.setChannel(baseEvent.getChannel());
        activityData.setAppVersion(baseEvent.getAppVersion());
        activityData.setIpAddress(baseEvent.getIpAddress());
        activityData.setActivityStatus(baseEvent.getActivityStatus());
        activityData.setFailReason(baseEvent.getFailReason());

        activityData.setActivityType(BuyActivityEnums.CLICK_PURCHASE_BUTTON_AT_FUND_FACT_SHEET_SCREEN.getEvent());

        activityData.setFundName(alternativeBuyRequest.getFundName());
        activityData.setVerifyFlag(alternativeBuyRequest.getProcessFlag());
        activityData.setReason(baseEvent.getFailReason());
        activityData.setFundClass(alternativeBuyRequest.getFundEnglishClassName());

        logActivityService.createLog(activityData);
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
        BuyActivityLog activityData = new BuyActivityLog(
                correlationId, String.valueOf(System.currentTimeMillis()),
                BuyActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        BaseEvent baseEvent = logActivityService.buildCommonData(crmId, ipAddress, response);

        activityData.setCrmId(baseEvent.getCrmId());
        activityData.setChannel(baseEvent.getChannel());
        activityData.setAppVersion(baseEvent.getAppVersion());
        activityData.setIpAddress(baseEvent.getIpAddress());
        activityData.setActivityStatus(baseEvent.getActivityStatus());
        activityData.setFailReason(baseEvent.getFailReason());

        activityData.setActivityType(BuyActivityEnums.ENTER_PIN_IS_CORRECT.getEvent());

        activityData.setStatus(ProductsExpServiceConstant.SUCCESS.equalsIgnoreCase(baseEvent.getActivityStatus()) ?
                ActivityLogStatus.COMPLETED.getStatus() : ActivityLogStatus.FAILED.getStatus());
        activityData.setUnitHolder(requestBody.getPortfolioNumber());
        activityData.setOrderId(response.getData() != null ? response.getData().getOrderId() : null);
        activityData.setFundName(requestBody.getFundName());
        activityData.setFundClass(!StringUtils.isEmpty(requestBody.getFundThaiClassName()) ? requestBody.getFundThaiClassName() : requestBody.getFundEnglishClassName());
        activityData.setAmount(requestBody.getOrderAmount());
        activityData.setFromBankAccount(requestBody.getFromAccount().getAccountId());

        logActivityService.createLog(activityData);
    }
}
