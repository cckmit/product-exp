package com.tmb.oneapp.productsexpservice.model.homeloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "description"
})
@Data
public class StatusResponse {

    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;
}
