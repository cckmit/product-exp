package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "account"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeLoanFullInfoResponse {

    @JsonProperty("status")
    private StatusResponse status;
    @JsonProperty("additional_status")
    private AdditionalStatus additionalStatus;
    @JsonProperty("account")
    private Account account;
    @JsonProperty("product_config")
    ProductConfig productConfig;

}
