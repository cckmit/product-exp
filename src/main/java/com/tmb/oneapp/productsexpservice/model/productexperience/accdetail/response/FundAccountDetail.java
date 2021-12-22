package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response;


import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FundAccountDetail  {



   private FundDetail accountDetail;

    private List<FundRuleInfoList> fundRuleInfoList;
}
