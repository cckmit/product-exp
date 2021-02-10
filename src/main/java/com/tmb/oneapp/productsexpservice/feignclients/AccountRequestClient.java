package com.tmb.oneapp.productsexpservice.feignclients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * The interface Account request client.
 */
@FeignClient(name = "${account.service.name}", url = "${account.service.url}")
public interface AccountRequestClient {
    /**
     * Gets port list.
     *
     * @param headers the headers
     * @param cardId  the card id
     * @return the port list
     */
    @GetMapping(value = "${account.service.account.url}", consumes = "application/json", produces = "application/json")
    public String getPortList(@RequestHeader Map<String, String> headers, @PathVariable("CRM_ID") String cardId);
}
