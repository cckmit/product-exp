package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class DepositSuccessResponse {

    @JsonProperty("status")
    private StatusResponse status;
    @JsonProperty("to_acct_name")
    private String toAcctName;
    @JsonProperty("transaction_amount")
    private String transactionAmount;
    @JsonProperty("debit_current_balance")
    private String debitCurrentBalance;
    @JsonProperty("debit_available_balance")
    private String debitAvailableBalance;
    @JsonProperty("fee_amount")
    private String feeAmount;
    @JsonProperty("teller_id")
    private String tellerId;
    @JsonProperty("flag_fee_reg")
    private String flagFeeReg;
    @JsonProperty("waive_product_code")
    private String waiveProductCode;
    @JsonProperty("amount_waived")
    private String amountWaived;
    @JsonProperty("waive_remaining")
    private String waiveRemaining;
    @JsonProperty("waive_used")
    private String waiveUsed;
    @JsonProperty("waive_flag")
    private String waiveFlag;
}