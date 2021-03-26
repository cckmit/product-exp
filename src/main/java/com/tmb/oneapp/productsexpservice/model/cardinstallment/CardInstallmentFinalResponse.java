package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
"status",
"credit_card"
})
@Data
public class CardInstallmentFinalResponse {

@JsonProperty("status")
private StatusResponse status;
@JsonProperty("credit_card")
private CreditCardModel creditCard;

}

