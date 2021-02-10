package com.tmb.oneapp.productsexpservice.model.fundsummarydata.request;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@NotNull
public class UnitHolder {
    @NotNull
    private String unitHolderNo;
}
