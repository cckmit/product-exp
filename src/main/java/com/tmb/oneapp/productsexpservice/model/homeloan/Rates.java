package com.tmb.oneapp.productsexpservice.model.homeloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "current_interest_rate",
        "original_interest_rate"
})
@Data
public class Rates {

    @JsonProperty("current_interest_rate")
    private String currentInterestRate;
    @JsonProperty("original_interest_rate")
    private String originalInterestRate;
}