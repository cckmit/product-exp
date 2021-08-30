package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.customer.accountdetail.request.AccountDetailRequest;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.model.loan.AccountSaving;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;

@FeignClient(name = "${feign.customers.exp.service.name}", url = "${feign.customers.exp.service.url}")
public interface CustomerExpServiceClient {

    @GetMapping(value = "/apis/customer/accounts/saving")
    String getAccountSaving(
            @RequestHeader(value = HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader("x-crmid") String crmId
    );

    @GetMapping(value = "/apis/customer/accounts/saving")
    ResponseEntity<TmbOneServiceResponse<AccountSaving>> getCustomerAccountSaving(
            @RequestHeader(value = HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader("X-CRMID") String crmId
    );

    @GetMapping(value = "/apis/customer/accounts/creditcard-group")
    ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> getCustomerCreditCard(
            @RequestHeader(value = HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader("X-CRMID") String crmId
    );

    @PostMapping(value = "/apis/customer/accounts/details")
    String getAccountDetail(
            @RequestHeader(value = HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody AccountDetailRequest requestBody
    );
}
