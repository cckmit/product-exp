package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status_code",
        "severity",
        "status_desc"
})
@Getter
@Setter
public class AdditionalStatus {

    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("status_desc")
    private String statusDesc;
}
