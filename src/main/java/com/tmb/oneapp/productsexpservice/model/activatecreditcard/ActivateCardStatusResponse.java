package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.ErrorStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status_code"
})
public class ActivateCardStatusResponse {

    @JsonProperty("status_code")
    private Integer statusCode;
    
    @JsonProperty("error_status")
    private List<ErrorStatus> errorStatus;
}