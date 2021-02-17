package com.tmb.oneapp.productsexpservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;

/**
 * InvestmentClient interface consume account details from investment service
 */
@FeignClient(name = "${feign.customer.exp.service.name}", url = "${feign.customer.exp.service.base.url}")
public interface CustomerExpRequestClient {

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers  the headers
     * @param crmId the fund code
     * @return the fund summary response
     */
    @GetMapping(value = "${feign.customer.exp.service.saving.url}")
    public String callCustomerExpService(@RequestHeader Map<String, String> headers
            , @RequestHeader("X-CRMID") String crmId);
}
