package com.tmb.oneapp.productsexpservice.model.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOpenPortfolioResponse {
    private TermAndConditionResponseBody termsConditions;
    private CustomerInfo customerInfo;
    private List<DepositAccount> depositAccountList;
}
