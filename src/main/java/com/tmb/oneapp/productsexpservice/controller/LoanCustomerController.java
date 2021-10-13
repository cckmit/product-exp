package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerSubmissionResponse;
import com.tmb.oneapp.productsexpservice.service.LoanCustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Api(tags = "Customer Profile")
public class LoanCustomerController {

    private static final TMBLogger<LoanCustomerController> logger = new TMBLogger<>(LoanCustomerController.class);
    private final LoanCustomerService loanCustomerService;

    @LogAround
    @ApiOperation("Get customer profile")
    @GetMapping(value = "/get-customer-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanCustomerResponse>> getLoanCustomerProfile(@Valid @RequestHeader(name = "X-CRMID") String crmId,
                                                                                              @Valid @RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
                                                                                              @Valid LoanCustomerRequest request) {

        TmbOneServiceResponse<LoanCustomerResponse> customerProfileResp = new TmbOneServiceResponse<>();

        try {
            LoanCustomerResponse loanCustomerResponse = loanCustomerService.getCustomerProfile(correlationId, request, crmId);
            customerProfileResp.setData(loanCustomerResponse);
            customerProfileResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(customerProfileResp);

        } catch (Exception e) {
            logger.error("Error while getConfig: {}", e);
            customerProfileResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(customerProfileResp);
        }

    }

    @LogAround
    @ApiOperation("Submission customer profile")
    @PostMapping(value = "/submission-customer-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanCustomerSubmissionResponse>> saveCustomerProfile(@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
                                                                                                     @Valid @RequestBody LoanCustomerSubmissionRequest request) {
        TmbOneServiceResponse<LoanCustomerSubmissionResponse> saveCustomerProfileResp = new TmbOneServiceResponse<>();

        try {
            LoanCustomerSubmissionResponse loanCustomerSubmissionResponse = loanCustomerService.saveCustomerSubmission(request);
            saveCustomerProfileResp.setData(loanCustomerSubmissionResponse);
            saveCustomerProfileResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(saveCustomerProfileResp);

        } catch (Exception e) {
            logger.error("Error while submission customer profile : {}", e);
            saveCustomerProfileResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(saveCustomerProfileResp);
        }

    }
}
