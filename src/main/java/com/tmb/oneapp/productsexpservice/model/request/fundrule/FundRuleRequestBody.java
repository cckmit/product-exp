package com.tmb.oneapp.productsexpservice.model.request.fundrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundRuleRequestBody {
    @NotNull
    private String fundHouseCode;
    @NotNull
    private String fundCode;
    private String tranType;
}

