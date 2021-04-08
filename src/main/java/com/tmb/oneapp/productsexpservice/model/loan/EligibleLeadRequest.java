package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "groupAccountId",
        "disbursementDate"
})

public class EligibleLeadRequest {

    @JsonProperty("groupAccountId")
    private String groupAccountId;
    @JsonProperty("disbursementDate")
    private String disbursementDate;
}
