package com.tmb.oneapp.productsexpservice.model.openportfolio.response;

import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioResponse {

    private AccountPurposeResponseBody accountPurposeResponseBody;

    private AccountRedeemResponseBody accountRedeemResponseBody;
}
