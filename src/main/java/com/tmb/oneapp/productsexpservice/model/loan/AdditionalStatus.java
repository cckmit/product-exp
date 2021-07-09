package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class AdditionalStatus {

    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("server_status_code")
    private String serverStatusCode;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("status_desc")
    private String statusDesc;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("namespace")
    private String namespace;
}
