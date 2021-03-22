package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.LoanData;
import com.tmb.oneapp.productsexpservice.model.request.LoanStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * HirePurchaseExperienceClient to retrieve data from hire purchase system
 */
@FeignClient(name = "${hp.exp.service.name}", url = "${hp.exp.service.url}")
public interface HirePurchaseExperienceClient {

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param request the fund code
     * @return the fund summary response
     */
    @PostMapping(value = "/apis/hpservice/loan-status/application-list")
    ResponseEntity<TmbOneServiceResponse<LoanData>> postLoanStatusApplicationList(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody LoanStatusRequest request);


    @PostMapping(value = "/apis/hpservice/loan-status/application-detail")
    ResponseEntity<TmbOneServiceResponse<LoanData>> postLoanStatusApplicationDetail(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody LoanStatusRequest request);


}
