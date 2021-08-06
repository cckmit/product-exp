package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonPropertyOrder({
        "id",
        "type",
        "branch_id",
        "product_id",
        "title",
        "currency",
        "status",
        "debit_account",
        "balances",
        "payment",
        "credit_limit",
        "rates"
})
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class Account {

    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("branch_id")
    private String branchId;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("debit_account")
    private DebitAccount debitAccount;
    @JsonProperty("balances")
    private Balances balances;
    @JsonProperty("payment")
    private Payment payment;
    @JsonProperty("credit_limit")
    private CreditLimit creditLimit;
    @JsonProperty("rates")
    private Rates rates;
    @JsonProperty("direct_debit")
    private DirectDebit directDebit;
}