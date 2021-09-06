package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.oneapp.productsexpservice.activitylog.portfolio.enums.OpenPortfolioActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
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
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.OPEN_PORTFOLIO.getActivityTypeId());
        activityData.setInitialOpenPortfolio(initialOpenPortfolio);
        activityData.setReasonValue(reasonValue);
        logActivityService.createLog(activityData);
    }

    public void acceptTermAndCondition(String correlationId, String crmId, String value) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.ACCEPT_TERM_AND_CONDITION.getActivityTypeId());
        activityData.setValue(value);
        logActivityService.createLog(activityData);
    }

    public void clickConfirm(String correlationId, String crmId, OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.CLICK_CONFIRM_BUTTON.getActivityTypeId());
        activityData.setScoreValue(openPortfolioActivityLogRequest.getScoreValue());
        activityData.setNickname(openPortfolioActivityLogRequest.getNickname());
        activityData.setPurposeOfInvestment(openPortfolioActivityLogRequest.getPurposeOfInvestment());
        activityData.setReceivingAccount(openPortfolioActivityLogRequest.getReceivingAccount());
        activityData.setAddress(openPortfolioActivityLogRequest.getAddress());
        logActivityService.createLog(activityData);
    }

    public void enterCorrectPin(String correlationId, String crmId, String status, String portfolioNumber, String portfolioNickname) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        activityData.setStatus(status);
        activityData.setPortfolioNumber(portfolioNumber);
        activityData.setPortfolioNickname(portfolioNickname);
        logActivityService.createLog(activityData);
    }

    private OpenPortfolioActivityLog initialActivityLogData(String correlationId, String crmId, String activityTypeId) {
        OpenPortfolioActivityLog activityData = new OpenPortfolioActivityLog(correlationId, String.valueOf(System.currentTimeMillis()), activityTypeId);
        activityData.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
        activityData.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        return activityData;
    }
}
