package com.tmb.oneapp.productsexpservice.model.loan;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status_code",
        "error_status"
})
@Generated("jsonschema2pojo")
public class ErrorResponse {

    @JsonProperty("status_code")
    public String statusCode;
    @JsonProperty("error_status")
    public List<ErrorStatus> errorStatus = null;

}
