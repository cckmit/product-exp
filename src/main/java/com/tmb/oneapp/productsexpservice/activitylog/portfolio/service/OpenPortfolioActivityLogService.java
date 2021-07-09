package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenPortfolioActivityLogService {

    private LogActivityService logActivityService;

    @Autowired
    public OpenPortfolioActivityLogService(LogActivityService logActivityService) {
        this.logActivityService = logActivityService;
    }

    public void openPortfolio(String correlationId, String crmId, String initialOpenPortfolio, String reasonValue) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING_OPEN_PORTFOLIO);
        activityData.setInitialOpenPortfolio(initialOpenPortfolio);
        activityData.setReasonValue(reasonValue);
        logActivityService.createLog(activityData);
    }

    private OpenPortfolioActivityLog initialActivityLogData(String correlationId, String crmId, String activityTypeId) {
        OpenPortfolioActivityLog activityData = new OpenPortfolioActivityLog(correlationId, String.valueOf(System.currentTimeMillis()), activityTypeId);
        activityData.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
        activityData.setCrmId(UtilMap.fillUpCrmIdFormat(crmId));
        return activityData;
    }
}
