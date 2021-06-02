package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import com.tmb.oneapp.productsexpservice.service.CustomerProfileService;
import com.tmb.oneapp.productsexpservice.service.FlexiLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RequiredArgsConstructor
@RequestMapping("/lending")
@Api(tags = "Flexi Loan C2G")
@RestController
public class FlexiLoanController {

    private static final TMBLogger<CustomerProfileService> logger = new TMBLogger<>(CustomerProfileService.class);
    private final FlexiLoanService flexiLoanService;

    @LogAround
    @ApiOperation("Flexi loan submission info")
    @GetMapping(value = "/submission/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> getSubmissionInfo(@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
                                                                                           @Valid SubmissionInfoRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        logger.info("Get flexi loan submission info for correlation id: {}", correlationId);

        TmbOneServiceResponse<SubmissionInfoResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            SubmissionInfoResponse response = flexiLoanService.getSubmissionInfo(request);
            oneTmbOneServiceResponse.setData(response);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while submission info : {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }
}
