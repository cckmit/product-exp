package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "original_loan",
        "principal",
        "ledger",
        "outstanding",
        "available",
        "current",
        "accrued_interest",
        "payoff"
})
@Data
public class Balances {

    @JsonProperty("original_loan")
    private String originalLoan;
    @JsonProperty("principal")
    private String principal;
    @JsonProperty("ledger")
    private String ledger;
    @JsonProperty("outstanding")
    private String outstanding;
    @JsonProperty("available")
    private String available;
    @JsonProperty("current")
    private String current;
    @JsonProperty("accrued_interest")
    private String accruedInterest;
    @JsonProperty("payoff")
    private String payoff;

}