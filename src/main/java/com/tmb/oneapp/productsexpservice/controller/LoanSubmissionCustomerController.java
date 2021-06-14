package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmitRegisterRequest;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-submission")
@Api(tags = "LoanSubmission Service")
public class LoanSubmissionCustomerController {
    private final LoanSubmissionCustomerService loanSubmissionCustomerService;

    private static final TMBLogger<LoanSubmissionCustomerController> logger =
            new TMBLogger<>(LoanSubmissionCustomerController.class);


    @GetMapping(value = "/get-customer-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get customer info")
    public ResponseEntity<TmbOneServiceResponse<LoanSubmissionResponse>> getCustomerInfo(@Valid LoanSubmissionRequest request) {
        TmbOneServiceResponse<LoanSubmissionResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {

            LoanSubmissionResponse loanSubmissionResponse = loanSubmissionCustomerService.getCustomerInfo(request.getCaID());
            oneTmbOneServiceResponse.setData(loanSubmissionResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while getCustomerInfo: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }

    @PostMapping(value = "/submit-register", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get customer info")
    public ResponseEntity<TmbOneServiceResponse<LoanSubmitRegisterRequest>> submitRegister(@Valid LoanSubmitRegisterRequest loanSubmitRegisterRequest) {
        TmbOneServiceResponse<LoanSubmitRegisterRequest> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {

            //LoanSubmitRegisterRequest response = loanSubmissionCustomerService.submitRegisterApplication();
           // oneTmbOneServiceResponse.setData(response);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);

        }catch (Exception e) {
            logger.error("Error while getCustomerInfo: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }
}
