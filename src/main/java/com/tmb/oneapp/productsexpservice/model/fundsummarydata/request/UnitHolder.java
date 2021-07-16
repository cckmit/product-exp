package com.tmb.oneapp.productsexpservice.model.fundsummarydata.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@NotNull
public class UnitHolder {

    @NotNull
    @JsonProperty(value = "unitHolderNo")
    private String unitHolderNumber;
}
