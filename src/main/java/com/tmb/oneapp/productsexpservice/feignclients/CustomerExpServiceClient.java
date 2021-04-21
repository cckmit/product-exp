package com.tmb.oneapp.productsexpservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CORRELATION_ID;

@FeignClient(name = "${feign.customers.exp.service.name}", url = "${feign.customers.exp.service.url}")
public interface CustomerExpServiceClient {

    @GetMapping(value = "/apis/customer/accounts/saving")
    public String getAccountSaving(
            @RequestHeader(value = X_CORRELATION_ID) String correlationId,
            @RequestHeader("X-CRMID") String crmId
    );
}
