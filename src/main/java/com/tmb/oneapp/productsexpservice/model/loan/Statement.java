package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)

@Getter
@Setter
@NoArgsConstructor
public class Statement {

    @JsonProperty("sequence_no")
    private String sequenceNo;
    @JsonProperty("transaction_date")
    private String transactionDate;
    @JsonProperty("transaction_code")
    private String transactionCode;
    @JsonProperty("loan_balance_amount")
    private String loanBalanceAmount;
    @JsonProperty("interest_amount")
    private String interestAmount;
    @JsonProperty("transaction_amount")
    private String transactionAmount;
    @JsonProperty("interest_rate")
    private String interestRate;
    @JsonProperty("outstanding_balance")
    private String outstandingBalance;
    @JsonProperty("outstanding_interest")
    private String outstandingInterest;
    @JsonProperty("fee_amount")
    private String feeAmount;
}