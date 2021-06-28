package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ApplyPersonalLoan;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionInstantLoanCalUWService;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lending")
@Api(tags = "Personal Loan")
public class PersonalLoanController {

    private final PersonalLoanService personalLoanService;
    private final LoanSubmissionInstantLoanCalUWService loanCalUWService;

    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);
    private static final HttpHeaders responseHeaders = new HttpHeaders();

    @GetMapping("/get_preload")
    @LogAround
    @ApiOperation(value = "check flag preload from config mongodb")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> checkPreload(@Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
                                                                                   @Valid LoanPreloadRequest loanPreloadRequest) {
        TmbOneServiceResponse<LoanPreloadResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            oneTmbOneServiceResponse.setData(personalLoanService.checkPreload(correlationId, loanPreloadRequest));
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while getConfig: {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @GetMapping(value = "/get-preload-brms", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get preload brms")
    public ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> checkCalUW(@Valid InstantLoanCalUWRequest instantLoanCalUWRequest) {
        TmbOneServiceResponse<InstantLoanCalUWResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            InstantLoanCalUWResponse instantLoanCalUWResponse = loanCalUWService.checkCalculateUnderwriting(instantLoanCalUWRequest);
            oneTmbOneServiceResponse.setData(instantLoanCalUWResponse);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while check under writing: {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }


    @GetMapping(value = "/get-product-loan-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product loan list")
    public ResponseEntity<TmbOneServiceResponse<ApplyPersonalLoan>> getProductList(@Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
                                                                                   @RequestParam("search") @Valid String search ) {
        TmbOneServiceResponse<ApplyPersonalLoan> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ApplyPersonalLoan productDataLoanList = personalLoanService.getProductsLoan(correlationId,search);
            oneTmbOneServiceResponse.setData(productDataLoanList);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get product list: {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @GetMapping(value = "/get-product-credit-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product credit list")
    public ResponseEntity<TmbOneServiceResponse<List<ProductData>>> getProductCreditList(@Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
                                                                                         @RequestParam("search") @Valid String search) {
        TmbOneServiceResponse<List<ProductData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            List<ProductData> productDataCreditList = personalLoanService.getProductsCredit(correlationId,search);
            oneTmbOneServiceResponse.setData(productDataCreditList);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get product credit list: {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    private TmbStatus getStatusFailed() {
        return new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService());
    }


    private TmbStatus getStatusSuccess() {
        return new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE);
    }

    private void setHeader() {
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
    }


}
