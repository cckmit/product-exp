package com.tmb.oneapp.productsexpservice.model.response.accdetail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response.ViewAipResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountDetail {

    private AccountDetail accountDetail;

    private List<FundRuleInfoList> fundRuleInfoList;

    private ViewAipResponseBody viewAip;
}
