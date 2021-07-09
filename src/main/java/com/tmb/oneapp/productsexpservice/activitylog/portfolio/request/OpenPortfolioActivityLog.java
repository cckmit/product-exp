package com.tmb.oneapp.productsexpservice.activitylog.portfolio.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPortfolioActivityLog extends ActivityLogs {

    /* Open portfolio */
    @JsonProperty(value = "initial_open_portfolio")
    private String initialOpenPortfolio;

    @JsonProperty(value = "reason_value")
    private String reasonValue;

    /* Accept term and condition */
    private String value;

    /* Click confirm */
    @JsonProperty(value = "score_value")
    private String scoreValue;

    private String nickname;

    @JsonProperty(value = "purpose_of_investment")
    private String purposeOfInvestment;

    @JsonProperty(value = "receiving_account")
    private String receivingAccount;

    private String address;

    public OpenPortfolioActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
