package com.tmb.oneapp.productsexpservice.model.request.accdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRq {
    private String unitHolderNo;
    private String fundHouseCode;
    private String fundCode;
    private String tranType;
    private String serviceType;
}
