package com.tmb.oneapp.productsexpservice.model.request.fundpayment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundPaymentDetailRq extends FundRuleRequestBody {
    private String crmId;
}
