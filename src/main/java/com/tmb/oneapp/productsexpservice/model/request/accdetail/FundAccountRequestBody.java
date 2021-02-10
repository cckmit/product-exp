package com.tmb.oneapp.productsexpservice.model.request.accdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequestBody {
    @JsonProperty("UnitHolderNo")
    private String unitHolderNo;
    @JsonProperty("FundCode")
    private String fundCode;
    @JsonProperty("ServiceType")
    private String serviceType;
}
