package com.tmb.oneapp.productsexpservice.model.request.stmtrequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderStmtByPortRq {
    @NotNull
    private String portfolioNumber;
    @NotNull
    private String fundCode;
    @NotNull
    private String rowStart;
    @NotNull
    private String rowEnd;
}
