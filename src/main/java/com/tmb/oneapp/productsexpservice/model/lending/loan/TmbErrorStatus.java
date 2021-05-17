package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Getter
@Setter
public class TmbErrorStatus {
    private String code;
    private String message;
    private String service;
}
