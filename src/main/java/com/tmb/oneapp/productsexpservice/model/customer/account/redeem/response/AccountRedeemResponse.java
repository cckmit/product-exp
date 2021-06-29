package com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRedeemResponse {

    private Status status;

    private AccountRedeemResponseBody data;
}
