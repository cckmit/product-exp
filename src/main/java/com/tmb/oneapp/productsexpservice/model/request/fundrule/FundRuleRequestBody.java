package com.tmb.oneapp.productsexpservice.model.request.fundrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundRuleRequestBody {
    @NotNull
    private String fundHouseCode;
    @NotNull
    private String fundCode;
    private String tranType;
}

