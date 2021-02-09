package com.tmb.oneapp.productsexpservice.model.request.fundrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundRuleRequestBody {
    private String fundHouseCode;
    private String fundCode;
    private String tranType;
}

