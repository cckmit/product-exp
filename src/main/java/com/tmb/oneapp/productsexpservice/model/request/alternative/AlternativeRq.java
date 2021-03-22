package com.tmb.oneapp.productsexpservice.model.request.alternative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlternativeRq {
    private String fundHouseCode;
    private String fundCode;
    @NotNull
    private String crmId;
    private String unitHolderNo;
    @NotNull
    private String orderType;
    private String processFlag;
}
