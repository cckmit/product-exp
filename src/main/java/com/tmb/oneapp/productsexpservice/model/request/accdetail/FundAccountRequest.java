package com.tmb.oneapp.productsexpservice.model.request.accdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequest {

    @NotNull
    @JsonProperty(value = "unitHolderNo")
    private String unitHolderNumber;

    @NotNull
    private String fundHouseCode;

    @NotNull
    private String fundCode;

    @NotNull
    private String tranType;

    @NotNull
    private String serviceType;
}
