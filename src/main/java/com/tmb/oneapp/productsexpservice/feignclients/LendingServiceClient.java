package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * LendingServiceClient to retrieve lending data
 */
@FeignClient(name = "${lending.service.name}", url = "${lending.service.url}")
public interface LendingServiceClient {

    /**
     * Call RSL System to get application status
     *
     * @return RSL application statuses
     */
    @GetMapping(value = "/apis/lending-service/rsl/status")
    public ResponseEntity<TmbOneServiceResponse<List<LendingRslStatusResponse>>> getLendingRslStatus(
            @RequestHeader(X_CORRELATION_ID) String correlationId,
            @RequestHeader(HEADER_CITIZEN_ID) String citizenId,
            @RequestHeader(HEADER_MOBILE_NO) String mobileNo);


}
