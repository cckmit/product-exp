package com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FatcaRequest {

    @JsonProperty(value = "fatca_answer1")
    private String answerOne;

    @JsonProperty(value = "fatca_answer2")
    private String answerTwo;
}
