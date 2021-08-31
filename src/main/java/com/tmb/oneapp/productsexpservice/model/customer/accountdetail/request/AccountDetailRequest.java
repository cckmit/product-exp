package com.tmb.oneapp.productsexpservice.model.customer.accountdetail.request;

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
