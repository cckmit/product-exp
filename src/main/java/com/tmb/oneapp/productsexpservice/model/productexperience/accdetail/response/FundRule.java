package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundRule extends FundRuleInfoList {
}
