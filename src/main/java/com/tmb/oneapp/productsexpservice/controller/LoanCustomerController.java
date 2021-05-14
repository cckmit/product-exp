package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import com.tmb.oneapp.productsexpservice.service.LoanCustomerService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Api(tags = "Customer Profile")
public class LoanCustomerController {

    private static final TMBLogger<LoanCustomerController> log = new TMBLogger<>(LoanCustomerController.class);
    private final LoanCustomerService loanCustomerService;

    @LogAround
    @ApiOperation("Get customer profile")
    @GetMapping(value = "/get-customer-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanCustomerResponse>> getLoanStatement(@Valid @RequestBody LoanCustomerRequest request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanCustomerResponse> response = new TmbOneServiceResponse<>();
        LoanCustomerResponse loanCustomerResponse = loanCustomerService.getCustomerProfile();
        response.setData(loanCustomerResponse);

        try {
            return ResponseEntity.ok().headers(responseHeaders).body(response);

        } catch (Exception e) {
            log.error("Error while getting customer profile: {}", e);
            return ResponseEntity.badRequest().headers(responseHeaders).body(response);
        }

    }
}
