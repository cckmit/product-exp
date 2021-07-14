package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response.ViewAipResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FundAccountDetail {

    private AccountDetail accountDetail;

    private List<FundRuleInfoList> fundRuleInfoList;

    private ViewAipResponseBody viewAip;
}
