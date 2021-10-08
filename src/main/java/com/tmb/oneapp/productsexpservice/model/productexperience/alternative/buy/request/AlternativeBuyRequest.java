package com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlternativeBuyRequest {

    private String fundCode;

    @NotNull
    @JsonProperty(value = "unitHolderNo")
    private String unitHolderNumber;

    private String processFlag;

    private String fundName;

    private String fundEnglishClassName;

    private String fundHouseCode;

    private String tranType;

}
