package com.tmb.oneapp.productsexpservice.model.request.fundffs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FfsRequestBody {
    @NotNull
    private String fundHouseCode;
    @NotNull
    private String fundCode;
    @NotNull
    private String language;
    private String processFlag;
    private String orderType;
    @NotNull
    private String crmId;
    private String unitHolderNo;
}