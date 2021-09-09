package com.tmb.oneapp.productsexpservice.activitylog.portfolio.enums;

import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import lombok.Getter;

@Getter
public enum OpenPortfolioActivityEnums {

    OPEN_PORTFOLIO("101000701", ProductsExpServiceConstant.ACTIVITY_LOG_OPEN_PORTFOLIO_DESCRIPTION, "When user press on open portfolio button at my mutual fund screen or click on start button at on-boarding"),
    ACCEPT_TERM_AND_CONDITION("101000702", ProductsExpServiceConstant.ACTIVITY_LOG_OPEN_PORTFOLIO_DESCRIPTION, "When user accept term and condition"),
    CLICK_CONFIRM_BUTTON("101000703", ProductsExpServiceConstant.ACTIVITY_LOG_OPEN_PORTFOLIO_DESCRIPTION, "When user click confirm at New score screen with customer information"),
    ENTER_PIN_IS_CORRECT("101000704", ProductsExpServiceConstant.ACTIVITY_LOG_OPEN_PORTFOLIO_DESCRIPTION, "When enter PIN is correct ");

    private String activityTypeId;
    private String description;
    private String event;

    OpenPortfolioActivityEnums(String activityTypeId, String description, String event) {
        this.activityTypeId = activityTypeId;
        this.description = description;
        this.event = event;
    }
}
