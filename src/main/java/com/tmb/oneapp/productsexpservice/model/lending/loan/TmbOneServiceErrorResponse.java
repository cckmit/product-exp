package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Setter
@Getter
public class TmbOneServiceErrorResponse {
    private TmbErrorStatus status;
}
