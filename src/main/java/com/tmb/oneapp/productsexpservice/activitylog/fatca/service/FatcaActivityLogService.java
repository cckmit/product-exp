package com.tmb.oneapp.productsexpservice.activitylog.fatca.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.fatca.enums.FatcaActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.fatca.request.FatcaActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The fatca activity log service.
 */
@Service
public class FatcaActivityLogService {

    private final LogActivityService logActivityService;

    @Autowired
    public FatcaActivityLogService(LogActivityService logActivityService) {
        this.logActivityService = logActivityService;
    }

    /**
     * Generic Method to save activity log when click purchase button at fund fact sheet screen
     *
     * @param correlationId      the correlation id
     * @param crmId              the crm id
     * @param ipAddress          the ip address
     * @param fatcaFlag          the fatca flag
     * @param oneServiceResponse the TMB OneApp response
     * @return
     */
    @LogAround
    public void clickNextButtonAtFatcaQuestionScreen(String correlationId, String crmId, String ipAddress, String fatcaFlag,
                                                     TmbOneServiceResponse<FatcaResponseBody> oneServiceResponse) {
        FatcaActivityLog activityData = new FatcaActivityLog(
                correlationId, String.valueOf(System.currentTimeMillis()),
                FatcaActivityEnums.CLICK_NEXT_BUTTON_AT_FATCA_QUESTION_SCREEN.getActivityTypeId());
        BaseEvent baseEvent = logActivityService.buildCommonData(crmId, ipAddress, oneServiceResponse);

        activityData.setCrmId(baseEvent.getCrmId());
        activityData.setChannel(baseEvent.getChannel());
        activityData.setAppVersion(baseEvent.getAppVersion());
        activityData.setIpAddress(baseEvent.getIpAddress());
        activityData.setActivityStatus(baseEvent.getActivityStatus());
        activityData.setFailReason(baseEvent.getFailReason());

        activityData.setCompleteFatcaForm(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_YES);
        activityData.setFatcaFlag(fatcaFlag);

        logActivityService.createLog(activityData);
    }
}
