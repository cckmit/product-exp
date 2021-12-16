package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.BaseEvent;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.enums.OpenPortfolioActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The open portfolio activity log service.
 */
@Service
public class OpenPortfolioActivityLogService {

    private final LogActivityService logActivityService;

    @Autowired
    public OpenPortfolioActivityLogService(LogActivityService logActivityService) {
        this.logActivityService = logActivityService;
    }

    /**
     * Generic Method to save activity log when open portfolio
     *
     * @param correlationId        the correlation id
     * @param crmId                the crm id
     * @param ipAddress            the ip address
     * @param initialOpenPortfolio the initial open portfolio
     * @param reasonValue          the reason value
     * @return
     */
    @LogAround
    public void openPortfolio(String correlationId, String crmId, String ipAddress, String initialOpenPortfolio, String reasonValue) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, ipAddress, OpenPortfolioActivityEnums.OPEN_PORTFOLIO.getActivityTypeId());
        activityData.setInitialOpenPortfolio(initialOpenPortfolio);
        activityData.setReason(reasonValue);
        logActivityService.createLog(activityData);
    }

    /**
     * Generic Method to save activity log when accept term and condition
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param ipAddress     the ip address
     * @return
     */
    @LogAround
    public void acceptTermAndCondition(String correlationId, String crmId, String ipAddress) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, ipAddress, OpenPortfolioActivityEnums.ACCEPT_TERM_AND_CONDITION.getActivityTypeId());
        activityData.setValue(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_ACCEPT_TERM_AND_CONDITION);
        logActivityService.createLog(activityData);
    }

    /**
     * Generic Method to save activity log when click confirm at new score screen
     *
     * @param correlationId                   the correlation id
     * @param crmId                           the crm id
     * @param ipAddress                       the ip address
     * @param openPortfolioActivityLogRequest the open portfolio activity log request
     * @return
     */
    @LogAround
    public void clickConfirm(String correlationId, String crmId, String ipAddress, OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, ipAddress, OpenPortfolioActivityEnums.CLICK_CONFIRM_BUTTON.getActivityTypeId());
        activityData.setScore(openPortfolioActivityLogRequest.getScoreValue());
        activityData.setNickname(openPortfolioActivityLogRequest.getNickname());
        activityData.setPurposeOfInvestment(openPortfolioActivityLogRequest.getPurposeOfInvestment());
        activityData.setReceivingAccount(openPortfolioActivityLogRequest.getReceivingAccount());
        activityData.setAddress(openPortfolioActivityLogRequest.getAddress());
        logActivityService.createLog(activityData);
    }

    /**
     * Generic Method to save activity log when enter pin is correct
     *
     * @param correlationId     the correlation id
     * @param crmId             the crm id
     * @param ipAddress         the ip address
     * @param status            the status
     * @param portfolioNumber   the portfolio number
     * @param portfolioNickname the portfolio nickname
     * @return
     */
    @LogAround
    public void enterPinIsCorrect(String correlationId, String crmId, String ipAddress, String status, String portfolioNumber, String portfolioNickname) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, ipAddress, OpenPortfolioActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        activityData.setStatus(status);
        activityData.setPortfolioNumber(portfolioNumber);
        activityData.setPortfolioNickname(portfolioNickname);
        logActivityService.createLog(activityData);
    }

    private OpenPortfolioActivityLog initialActivityLogData(String correlationId, String crmId, String ipAddress, String activityTypeId) {
        OpenPortfolioActivityLog activityData = new OpenPortfolioActivityLog(correlationId, String.valueOf(System.currentTimeMillis()), activityTypeId);
        BaseEvent baseEvent = logActivityService.buildCommonData(crmId, ipAddress);

        activityData.setCrmId(baseEvent.getCrmId());
        activityData.setChannel(baseEvent.getChannel());
        activityData.setAppVersion(baseEvent.getAppVersion());
        activityData.setIpAddress(baseEvent.getIpAddress());
        activityData.setActivityStatus(baseEvent.getActivityStatus());
        activityData.setFailReason(baseEvent.getFailReason());

        return activityData;
    }
}
