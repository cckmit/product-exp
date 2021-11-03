package com.tmb.oneapp.productsexpservice.model.productexperience.financial.saveactivity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaveActivityRequest {

    @JsonProperty("activity_type_id")
    private String activityTypeId;

    @JsonProperty("biller_comp_code")
    private String billerCompCode;

    @JsonProperty("biller_name_en")
    private String billerNameEn;

    @JsonProperty("biller_name_th")
    private String billerNameTh;

    @JsonProperty("biller_ref1")
    private String billerRef1;

    @JsonProperty("biller_ref2")
    private String billerRef2;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("crm_id")
    private String crmId;

    @JsonProperty("financial_tranfer_amount")
    private String financialTranferAmount;

    @JsonProperty("financial_tranfer_cr_dr")
    private String financialTranferCrDr;

    @JsonProperty("financial_tranfer_memotransfer")
    private String financialTranferMemotransfer;

    @JsonProperty("financial_tranfer_ref_id")
    private String financialTranferRefId;

    @JsonProperty("from_account_nickname")
    private String fromAccountNickname;

    @JsonProperty("from_account_no")
    private String fromAccountNo;

    @JsonProperty("label_ref1_en")
    private String labelRef1En;

    @JsonProperty("label_ref1_th")
    private String labelRef1Th;

    @JsonProperty("label_ref2_en")
    private String labelRef2En;

    @JsonProperty("label_ref2_th")
    private String labelRef2Th;

    @JsonProperty("proxy_type")
    private String proxyType;

    @JsonProperty("proxy_value")
    private String proxyValue;

    @JsonProperty("reference_activity_type_id")
    private String referenceActivityTypeId;

    @JsonProperty("to_account_name")
    private String toAccountName;

    @JsonProperty("to_account_nickname")
    private String toAccountNickname;

    @JsonProperty("to_account_no")
    private String toAccountNo;

    @JsonProperty("to_bank_short_name")
    private String toBankShortName;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("transaction_status")
    private String transactionStatus;

}
