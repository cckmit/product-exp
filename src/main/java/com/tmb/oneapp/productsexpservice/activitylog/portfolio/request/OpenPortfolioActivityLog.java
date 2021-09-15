package com.tmb.oneapp.productsexpservice.activitylog.portfolio.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPortfolioActivityLog extends BaseEvent {

    /* Open portfolio */
    @JsonProperty(value = "initial_open_portfolio")
    private String initialOpenPortfolio;

    @JsonProperty(value = "reason")
    private String reason;

    /* Accept term and condition */
    private String value;

    /* Click confirm */
    @JsonProperty(value = "score")
    private String score;

    private String nickname;

    @JsonProperty(value = "purpose_of_investment")
    private String purposeOfInvestment;

    @JsonProperty(value = "receiving_account")
    private String receivingAccount;

    private String address;

    /* Enter correct pin */
    private String status;

    @JsonProperty(value = "portfolio_number")
    private String portfolioNumber;

    @JsonProperty(value = "portfolio_nickname")
    private String portfolioNickname;

    public OpenPortfolioActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
