package com.tmb.oneapp.productsexpservice.model.homeloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;



@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "amount",
        "interest_rate",
        "expired_date"
})
@Data
public class CreditLimit {

    @JsonProperty("amount")
    private String amount;
    @JsonProperty("interest_rate")
    private String interestRate;
    @JsonProperty("expired_date")
    private String expiredDate;
}
