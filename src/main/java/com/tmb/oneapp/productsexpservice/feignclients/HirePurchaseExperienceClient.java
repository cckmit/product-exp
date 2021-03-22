package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.LoanData;
import com.tmb.oneapp.productsexpservice.model.request.LoanStatusRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<TmbOneServiceResponse<LoanData>> postLoanStatusApplicationList(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody LoanStatusRequest request);


    @PostMapping(value = "/apis/hpservice/loan-status/application-detail")
    public ResponseEntity<TmbOneServiceResponse<LoanData>> postLoanStatusApplicationDetail(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody LoanStatusRequest request);


}
