package com.tmb.oneapp.productsexpservice.model.loan;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "deposit"
})
@Setter
@Getter

public class DepositRequest {

    @JsonProperty("deposit")
    private Deposit deposit;
}
