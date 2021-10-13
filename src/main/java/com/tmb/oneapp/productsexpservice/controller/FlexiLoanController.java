package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CheckSystemOffResponse;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.FlexiLoanConfirmRequest;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.FlexiLoanConfirmResponse;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import com.tmb.oneapp.productsexpservice.service.FlexiCheckSystemOffService;
import com.tmb.oneapp.productsexpservice.service.FlexiLoanConfirmService;
import com.tmb.oneapp.productsexpservice.service.FlexiLoanService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lending")
@Api(tags = "Flexi Loan")
public class FlexiLoanController {

    private static final TMBLogger<FlexiLoanService> logger = new TMBLogger<>(FlexiLoanService.class);

    private final FlexiLoanConfirmService flexiLoanConfirmService;
    private final FlexiLoanService flexiLoanService;
    private final FlexiCheckSystemOffService flexiCheckSystemOffService;

    @LogAround
    @ApiOperation("Flexi Loan Confirm")
    @ApiImplicitParams({
            @ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000000051187", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360018000167")
    })
    @PostMapping(value = "/flexiLoan/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FlexiLoanConfirmResponse>> submit(@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeaders,
                                                                                  @Valid @RequestBody FlexiLoanConfirmRequest request) {

        TmbOneServiceResponse<FlexiLoanConfirmResponse> response = new TmbOneServiceResponse<>();

        try {
            FlexiLoanConfirmResponse confirmResponse = flexiLoanConfirmService.confirm(requestHeaders, request);
            response.setData(confirmResponse);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error product-exp-service confirmFlexiLoan : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }

    }

    @LogAround
    @ApiOperation("Flexi loan submission info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Correlation-ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    @GetMapping(value = "/submission/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<SubmissionInfoResponse>> getSubmissionInfo(@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
                                                                                           @Valid SubmissionInfoRequest request) {
        logger.info("Get flexi loan submission info for correlation id: {}", correlationId);

        TmbOneServiceResponse<SubmissionInfoResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            SubmissionInfoResponse response = flexiLoanService.getSubmissionInfo(correlationId, request);
            oneTmbOneServiceResponse.setData(response);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while submission info : {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }

    }

    @LogAround
    @ApiOperation("check system off")
    @ApiImplicitParams({
            @ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    @GetMapping(value = "/checkSystemOff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<CheckSystemOffResponse>> checkSystemOff(@Valid @RequestHeader(HEADER_X_CORRELATION_ID) String correlationId) {

        logger.info("check system off  for correlation id: {}", correlationId);

        TmbOneServiceResponse<CheckSystemOffResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            CheckSystemOffResponse response = flexiCheckSystemOffService.checkSystemOff(correlationId);
            oneTmbOneServiceResponse.setData(response);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while check system off  : {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }

    }
}
