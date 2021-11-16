package com.tmb.oneapp.productsexpservice.model.activitylog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardEvent extends BaseEvent {
    @JsonProperty("card_number")
    private String cardNumber;
    @JsonProperty("result")
    private String result;
    @JsonProperty("method")
    private String method;
    @JsonProperty("reason")
    private String reasonForRequest;
    @JsonProperty("expiry_date")
    private String expiryDateForTempRequest;
    @JsonProperty("type")
    private String type;
    @JsonProperty("current_limit")
    private String currentLimit;
    @JsonProperty("new_limit")
    private String newLimit;
    @JsonProperty("plan")
    private String plan;
    @JsonProperty("amount_monthly_installment")
    private String amountMonthlyInstallment;
    @JsonProperty("total_interest_total_amount")
    private String totalAmountTotalIntrest;
    @JsonProperty("reason_code")
    private String reasonCode;
    @JsonProperty("transaction_description")
    private String transactionDescription;
    @JsonProperty("transection_date")
    private String transactionDate;
    @JsonProperty("loan_number")
    private String loanNumber;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("inita_vector")
    private String initailVector;
    

    public CreditCardEvent(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}

