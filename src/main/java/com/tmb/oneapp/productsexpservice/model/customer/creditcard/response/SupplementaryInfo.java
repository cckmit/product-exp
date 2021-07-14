package com.tmb.oneapp.productsexpservice.model.customer.creditcard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplementaryInfo {
    private String cardHolder;
    private int cardLimit;
    private String cardNumber;
}
