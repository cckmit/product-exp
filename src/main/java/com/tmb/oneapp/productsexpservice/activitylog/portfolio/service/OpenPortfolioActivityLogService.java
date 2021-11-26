package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.common.logger.LogAround;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.enums.OpenPortfolioActivityEnums;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLog;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
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
     * @param initialOpenPortfolio the initial open portfolio
     * @param reasonValue          the reason value
     * @return
     */
    @LogAround
    public void openPortfolio(String correlationId, String crmId, String initialOpenPortfolio, String reasonValue) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.OPEN_PORTFOLIO.getActivityTypeId());
        activityData.setInitialOpenPortfolio(initialOpenPortfolio);
        activityData.setReason(reasonValue);
        logActivityService.createLog(activityData);
    }

    /**
     * Generic Method to save activity log when accept term and condition
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param value         the value
     * @return
     */
    @LogAround
    public void acceptTermAndCondition(String correlationId, String crmId, String value) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.ACCEPT_TERM_AND_CONDITION.getActivityTypeId());
        activityData.setValue(value);
        logActivityService.createLog(activityData);
    }

    /**
     * Generic Method to save activity log when click confirm at new score screen
     *
     * @param correlationId                   the correlation id
     * @param crmId                           the crm id
     * @param openPortfolioActivityLogRequest the open portfolio activity log request
     * @return
     */
    @LogAround
    public void clickConfirm(String correlationId, String crmId, OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.CLICK_CONFIRM_BUTTON.getActivityTypeId());
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
     * @param status            the status
     * @param portfolioNumber   the portfolio number
     * @param portfolioNickname the portfolio nickname
     * @return
     */
    @LogAround
    public void enterPinIsCorrect(String correlationId, String crmId, String status, String portfolioNumber, String portfolioNickname) {
        OpenPortfolioActivityLog activityData = initialActivityLogData(correlationId, crmId, OpenPortfolioActivityEnums.ENTER_PIN_IS_CORRECT.getActivityTypeId());
        activityData.setStatus(status);
        activityData.setPortfolioNumber(portfolioNumber);
        activityData.setPortfolioNickname(portfolioNickname);
        logActivityService.createLog(activityData);
    }

    private OpenPortfolioActivityLog initialActivityLogData(String correlationId, String crmId, String activityTypeId) {
        OpenPortfolioActivityLog activityData = new OpenPortfolioActivityLog(correlationId, String.valueOf(System.currentTimeMillis()), activityTypeId);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_CHANNEL);
        activityData.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
        activityData.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        return activityData;
    }
}
