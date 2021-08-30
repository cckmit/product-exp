package com.tmb.oneapp.productsexpservice.model.customer.accountdetail.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.AddressModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailRequest {

    private String accountNo;

    private String accountType;

}
