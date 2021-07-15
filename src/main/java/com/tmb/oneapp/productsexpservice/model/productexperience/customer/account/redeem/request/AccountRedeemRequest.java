package com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRedeemRequest {

    @JsonProperty(value = "rmId")
    private String crmId;
}
