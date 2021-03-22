package com.tmb.oneapp.productsexpservice.model.cardinstallment;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusCode {

    @JsonProperty("status_code")
    private String code;
    @JsonProperty("error_status")
    private List<ErrorStatus> errorStatus;
}

