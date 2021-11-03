package com.tmb.oneapp.productsexpservice.model.productexperience.financial.sync.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinancalSyncRequest {

    @JsonProperty("activity_ref_id")
    private String activityRefId;

    @JsonProperty("activity_type_id")
    private String activityTypeId;

    @JsonProperty("activity_type_id_new")
    private String activityTypeIdNew;

    @JsonProperty("bankcode")
    private String bankcode;

    @JsonProperty("biller_balance")
    private String billerBalance;

    @JsonProperty("biller_customer_name")
    private String billerCustomerName;

    @JsonProperty("biller_ref1")
    private String billerRef1;

    @JsonProperty("biller_ref2")
    private String billerRef2;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("clearing_status")
    private String clearingStatus;

    @JsonProperty("comp_code")
    private String compCode;

    @JsonProperty("create_date")
    private String createDate;

    @JsonProperty("crm_id")
    private String crmId;

    @JsonProperty("error_cd")
    private String errorCd;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("fin_flex_values1")
    private String finFlexValues1;

    @JsonProperty("fin_linkage_id")
    private String finLinkageId;

    @JsonProperty("from_account_name")
    private String fromAccountName;

    @JsonProperty("from_account_nickname")
    private String fromAccountNickname;

    @JsonProperty("from_account_no")
    private String fromAccountNo;

    @JsonProperty("from_account_type")
    private String fromAccountType;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("note_to_recipient")
    private String noteToRecipient;

    @JsonProperty("proxy_id")
    private String proxyId;

    @JsonProperty("proxy_value")
    private String proxyValue;

    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("smart_flag")
    private String smartFlag;

    @JsonProperty("td_interest_amount")
    private String tdInterestAmount;

    @JsonProperty("td_maturity_date")
    private String tdMaturityDate;

    @JsonProperty("td_net_amount")
    private String tdNetAmount;

    @JsonProperty("td_penalty_amount")
    private String tdPenaltyAmount;

    @JsonProperty("td_tax_amount")
    private String tdTaxAmount;

    @JsonProperty("to_account_name")
    private String toAccountName;

    @JsonProperty("to_account_nickname")
    private String toAccountNickname;

    @JsonProperty("to_account_no")
    private String toAccountNo;

    @JsonProperty("to_account_type")
    private String toAccountType;

    @JsonProperty("transaction_Status")
    private String transactionStatus;

    @JsonProperty("transaction_amount")
    private String transactionAmount;

    @JsonProperty("transaction_balance")
    private String transactionBalance;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("transaction_fee")
    private String transactionFee;

    @JsonProperty("txn_cd")
    private String txnCd;

    @JsonProperty("txn_type")
    private String txnType;

}
