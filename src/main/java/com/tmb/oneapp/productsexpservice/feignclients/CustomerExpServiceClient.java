package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.customer.accountdetail.request.AccountDetailRequest;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.model.loan.AccountSaving;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request.FatcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${feign.customers.exp.service.name}", url = "${feign.customers.exp.service.url}")
public interface CustomerExpServiceClient {

    /**
     * Get submit to get customer account saving
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return String
     */
    @GetMapping(value = "/apis/customer/accounts/saving")
    String getAccountSaving(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId
    );

    /**
     * Get submit to get customer account saving
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return AccountSaving
     */
    @GetMapping(value = "/apis/customer/accounts/saving")
    ResponseEntity<TmbOneServiceResponse<AccountSaving>> getCustomerAccountSaving(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId
    );

    /**
     * Get submit to get customer credit card
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return CreditCardInformationResponse
     */
    @GetMapping(value = "/apis/customer/accounts/creditcard-group")
    ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> getCustomerCreditCard(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId
    );

    /**
     * Post submit to get account detail
     *
     * @param correlationId the correlation id
     * @param requestBody   the account detail request
     * @return String
     */
    @PostMapping(value = "/apis/customer/accounts/details")
    String getAccountDetail(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody AccountDetailRequest requestBody
    );

    /**
     * Post submit to create fatca form
     *
     * @param correlationId the correlation id
     * @param crmId         the customer id
     * @param fatcaRequest  the fatca request
     * @return FatcaResponseBody
     */
    @PostMapping(value = "/apis/customer/fatca/creation")
    ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> createFatcaForm(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestBody FatcaRequest fatcaRequest
    );
}
