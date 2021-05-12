package com.tmb.oneapp.productsexpservice.model.activitylog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlexiLoanEvent {
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("loan_amount")
    private String loanAmount;
    @JsonProperty("installment_amount")
    private String installmentAmount;
    @JsonProperty("tenor")
    private String tenor;
    @JsonProperty("payment_due_date")
    private String paymentDueDate;
}
