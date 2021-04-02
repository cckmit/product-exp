package com.tmb.oneapp.productsexpservice.model.homeloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "account_status",
        "contract_date"
})
public class Status {
    @JsonProperty("account_status")
    private String accountStatus;
    @JsonProperty("contract_date")
    private String contractDate;
}
