package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequestBody {

    private String portfolioNumber;

    private String fundCode;
}
