package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lending")
@Api(tags = "Personal Loan")
public class PersonalLoanController {


    private final PersonalLoanService personalLoanService;
    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);

    @GetMapping("/get_preload")
    @LogAround
    @ApiOperation(value = "check flage preload from config mongodb")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Correlation-ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> checkPreload(@Valid LoanPreloadRequest loanPreloadRequest) {
        TmbOneServiceResponse<LoanPreloadResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {

            oneTmbOneServiceResponse.setData(personalLoanService.checkPreload(loanPreloadRequest));
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

}
