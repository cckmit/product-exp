package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerSubmissionResponse;
import com.tmb.oneapp.productsexpservice.service.LoanCustomerService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.time.Instant;

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
    public ResponseEntity<TmbOneServiceResponse<LoanCustomerResponse>> getLoanStatement(@Valid LoanCustomerRequest request) throws ServiceException, RemoteException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanCustomerResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        LoanCustomerResponse loanCustomerResponse = loanCustomerService.getCustomerProfile(request);


        try {
            oneTmbOneServiceResponse.setData(loanCustomerResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        }catch (Exception e) {
            logger.error("Error while getConfig: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }

    @LogAround
    @ApiOperation("Get customer profile")
    @PostMapping(value = "/submission-customer-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanCustomerSubmissionResponse>> saveCustomerProfile(@Valid @RequestBody LoanCustomerSubmissionRequest request) throws ServiceException, RemoteException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanCustomerSubmissionResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        LoanCustomerSubmissionResponse loanCustomerSubmissionResponse = loanCustomerService.saveCustomerSubmission(request);

        try {
            oneTmbOneServiceResponse.setData(loanCustomerSubmissionResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        }catch (Exception e) {
            logger.error("Error while submission customer profile : {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }
}
