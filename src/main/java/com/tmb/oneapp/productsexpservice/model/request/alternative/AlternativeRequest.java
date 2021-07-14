package com.tmb.oneapp.productsexpservice.model.request.alternative;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
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
