package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "error_code",
        "namespace"
})
public class ErrorStatus {

    @JsonProperty("error_code")
    public String errorCode;
    @JsonProperty("namespace")
    public String namespace;

}
