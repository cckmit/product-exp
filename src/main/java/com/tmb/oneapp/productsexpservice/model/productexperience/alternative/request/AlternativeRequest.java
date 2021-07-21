package com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request;

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
public class AlternativeRequest {

    @NotNull
    private String crmId;

    private String fundHouseCode;

    private String fundCode;

    @JsonProperty(value = "unitHolderNo")
    private String unitHolderNumber;

    @NotNull
    private String orderType;

    private String processFlag;

    @JsonProperty(value = "fundClassNameThHub")
    private String fundClassThaiHubName;
}
