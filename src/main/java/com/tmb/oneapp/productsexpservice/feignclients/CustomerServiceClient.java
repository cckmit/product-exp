package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusCase;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${feign.customers.service.name}", url = "${feign.customers.service.url}")
public interface CustomerServiceClient {

    /**
     * Get first time usage status of customer for specified service.
     *
     * @param serviceTypeId service type ID
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/firstTimeUsage")
    public ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsage(
            @RequestHeader(value = "x-crmid") String crmId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId,
            @RequestHeader(value = "device-id") String deviceId);

    /**
     * Post first time usage status of customer for specified service.
     *
     * @param serviceTypeId service type ID
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @return String of insert first time usage status
     */
    @PostMapping(value = "/apis/customers/firstTimeUsage")
    public ResponseEntity<TmbOneServiceResponse<String>> postFirstTimeUsage(
            @RequestHeader(value = "x-crmid") String crmId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId,
            @RequestHeader(value = "device-id") String deviceId);

    /**
     * Get all case statuses for customer
     *
     * @param crmId customer ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/case/status/{CRM_ID}")
    public ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatus(
            @PathVariable("CRM_ID") String crmId
    );

}
