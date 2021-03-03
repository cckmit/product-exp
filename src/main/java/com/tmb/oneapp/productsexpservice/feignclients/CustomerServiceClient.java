package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusCase;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

@FeignClient(name = "${feign.customers.service.name}", url = "${feign.customers.service.url}")
public interface CustomerServiceClient {

    /**
     * Get first time usage status of customer for specified service.
     *
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @param serviceTypeId service type ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsage(
            @RequestHeader(value = X_CRMID) String crmId,
            @RequestHeader(value = DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Post first time usage status of customer for specified service.
     *
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @param serviceTypeId service type ID
     * @return String of insert first time usage status
     */
    @PostMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<String>> postFirstTimeUsage(
            @RequestHeader(value = X_CRMID) String crmId,
            @RequestHeader(value = DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Get all case statuses for customer
     *
     * @param crmId customer ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/case/status/{CRM_ID}")
    ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatus(
            @RequestHeader(value = X_CORRELATION_ID) String correlationId,
            @PathVariable("CRM_ID") String crmId
    );

}
