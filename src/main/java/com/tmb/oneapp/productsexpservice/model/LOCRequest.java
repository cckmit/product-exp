package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LOCRequest {

    @JsonProperty("NCBCustName")
    private String nCBCustName;
    @JsonProperty("NCBID")
    private String ncbid;
    @JsonProperty("NCBDateofbirth")
    private String nCBDateofbirth;
    @JsonProperty("NCBMobileNo")
    private String nCBMobileNo;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("NCBReferenceID")
    private String nCBReferenceID;
    @JsonProperty("NCBDateTime")
    private String nCBDateTime;
    @JsonProperty("ConsentbyCustomer")
    private String consentbyCustomer;
    @JsonProperty("appRefNo")
    private String appRefNo;

    @JsonProperty("crmId")
    private String crmId;
}
