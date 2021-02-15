package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "statusCode"
})
public class ActivateCardStatusResponse {

    @JsonProperty("statusCode")
    private Integer statusCode;
}