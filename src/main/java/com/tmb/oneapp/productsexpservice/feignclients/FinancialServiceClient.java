package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.financial.saveactivity.request.SaveActivityRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.financial.sync.request.FinancalSyncRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "${feign.financial.service.name}", url = "${feign.financial.service.url}")
public interface FinancialServiceClient {

    /**
     * sync log data
     *
     * @param correlationId correlationId
     * @param financalSyncRequest
     * @return String
     */
    @PostMapping(value = "/apis/finactivity/sync")
    ResponseEntity<TmbServiceResponse<String>> syncData(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody FinancalSyncRequest financalSyncRequest
    );
    /**
     * Save activity data
     *
     * @param correlationId correlationId
     * @param requestBody   EkycRiskCalculateRequest
     * @return String
     */
    @PostMapping(value = "/apis/finactivity/saveactivity")
    ResponseEntity<TmbServiceResponse<String>> saveActivity(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody SaveActivityRequest requestBody
    );
}
