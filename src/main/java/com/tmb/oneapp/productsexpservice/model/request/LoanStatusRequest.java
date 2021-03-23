package com.tmb.oneapp.productsexpservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class LoanStatusRequest {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("ReferenceNo")
    private String referenceNo;
    @JsonProperty("TransactionDateTime")
    private String transactionDateTime;
    @JsonProperty("ProviderUserID")
    private String providerUserID;
    @JsonProperty("NID")
    private String nID;
    @JsonProperty("MobileNo")
    private String mobileNo;
    @JsonProperty("Channel")
    private String channel;
    @JsonProperty("AppNo")
    private String appNo;
    @JsonProperty("Language")
    private String language;

}
