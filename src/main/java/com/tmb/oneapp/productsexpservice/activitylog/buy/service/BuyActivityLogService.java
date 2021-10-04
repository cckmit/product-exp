package com.tmb.oneapp.productsexpservice.activitylog.buy.service;

import com.tmb.common.logger.LogAround;
import com.tmb.oneapp.productsexpservice.activitylog.buy.enums.BuyActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.buy.request.BuyActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param alternativeBuyRequest the alternative buy request
     * @return
     */
    @LogAround
    public void clickPurchaseButtonAtFundFactSheetScreen(String correlationId, String crmId, AlternativeBuyRequest alternativeBuyRequest, String reason) {
        BuyActivityLog activityData = new BuyActivityLog(
                correlationId, String.valueOf(System.currentTimeMillis()),
                BuyActivityEnums.CLICK_PURCHASE_BUTTON_AT_FUND_FACT_SHEET_SCREEN.getActivityTypeId());

        String status = alternativeBuyRequest.getProcessFlag().equals(ProductsExpServiceConstant.PROCESS_FLAG_Y) ?
                ProductsExpServiceConstant.SUCCESS_MESSAGE : ProductsExpServiceConstant.FAILED_MESSAGE;

        activityData.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        activityData.setActivityStatus(status);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_APP_VERSION);
        activityData.setFailReason(reason);

        activityData.setFundName(alternativeBuyRequest.getFundName());
        activityData.setVerifyFlag(alternativeBuyRequest.getProcessFlag());
        activityData.setReason(reason);
        activityData.setFundClass(alternativeBuyRequest.getFundEnglishClassName());

        activityData.setActivityType(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING);
        logActivityService.createLog(activityData);
    }
}
