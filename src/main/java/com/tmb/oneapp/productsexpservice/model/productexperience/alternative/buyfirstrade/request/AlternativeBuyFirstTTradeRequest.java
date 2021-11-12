package com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buyfirstrade.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlternativeBuyFirstTTradeRequest {

    @NotBlank
    private String portfolioNumber;

    @NotBlank
    private String fundCode;

}
