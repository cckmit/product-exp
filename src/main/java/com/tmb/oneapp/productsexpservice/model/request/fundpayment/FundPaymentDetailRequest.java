package com.tmb.oneapp.productsexpservice.model.request.fundpayment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundPaymentDetailRequest extends FundRuleRequestBody {

    @NotNull
    private String crmId;
}
