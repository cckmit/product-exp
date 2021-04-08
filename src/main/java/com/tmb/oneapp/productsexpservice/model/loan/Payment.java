package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Setter
@Getter
public class Payment {

    @JsonProperty("monthly_payment_amount")
    private String monthlyPaymentAmount;
    @JsonProperty("next_payment_due_date")
    private String nextPaymentDueDate;
    @JsonProperty("next_payment_amount")
    private String nextPaymentAmount;
    @JsonProperty("next_payment_principal")
    private String nextPaymentPrincipal;
    @JsonProperty("next_payment_interest")
    private String nextPaymentInterest;
    @JsonProperty("last_payment_amount")
    private String lastPaymentAmount;
    @JsonProperty("last_payment_date")
    private String lastPaymentDate;
    @JsonProperty("current_terms")
    private String currentTerms;
    @JsonProperty("remaining_terms")
    private String remainingTerms;
    @JsonProperty("total_payment_amount")
    private String totalPaymentAmount;
    @JsonProperty("current_total_due_amount")
    private String currentTotalDueAmount;
    @JsonProperty("total_past_due_amount")
    private String totalPastDueAmount;
    @JsonProperty("late_charges_amount")
    private String lateChargesAmount;
    @JsonProperty("fee_due_amount")
    private String feeDueAmount;
    @JsonProperty("past_due_date")
    private String pastDueDate;
    @JsonProperty("past_due_payment_count")
    private String pastDuePaymentCount;
}
