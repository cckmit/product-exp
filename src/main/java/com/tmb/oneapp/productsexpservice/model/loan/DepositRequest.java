package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DepositRequest {

    @JsonProperty("from_account_id")
    private String fromAccountId;
    @JsonProperty("from_account_type")
    private String fromAccountType;
    @JsonProperty("to_account_id")
    private String toAccountId;
    @JsonProperty("to_account_type")
    private String toAccountType;
    @JsonProperty("amounts")
    private String amounts;
    @JsonProperty("transferred_date")
    private String transferredDate;
    @JsonProperty("waiver_code")
    private String waiverCode;
    @JsonProperty("expired_date")
    private String expiredDate;
    @JsonProperty("reference_code")
    private String referenceCode;
    @JsonProperty("model_type")
    private String modelType;
    @JsonProperty("order_no")
    private String orderNo;
}