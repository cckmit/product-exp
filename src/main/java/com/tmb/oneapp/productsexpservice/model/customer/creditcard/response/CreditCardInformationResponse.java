package com.tmb.oneapp.productsexpservice.model.customer.creditcard.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardInformationResponse {

    @JsonAlias("credit_cards")
    private List<CreditCard> creditCards;

    @JsonAlias("flash_cards")
    private List<CreditCard> flashCards;

}

