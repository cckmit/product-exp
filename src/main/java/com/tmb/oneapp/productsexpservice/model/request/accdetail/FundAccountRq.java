package com.tmb.oneapp.productsexpservice.model.request.accdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRq {
    @NotNull
    private String unitHolderNo;
    @NotNull
    private String fundHouseCode;
    @NotNull
    private String fundCode;
    @NotNull
    private String tranType;
    @NotNull
    private String serviceType;
}
