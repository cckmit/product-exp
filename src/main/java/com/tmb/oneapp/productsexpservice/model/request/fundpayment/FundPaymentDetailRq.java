package com.tmb.oneapp.productsexpservice.model.request.fundpayment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundPaymentDetailRq extends FundRuleRequestBody {
    @NotNull
    private String crmId;
}
