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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/get_preload")
    @LogAround
    @ApiOperation(value = "check flag preload from config mongodb")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Correlation-ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> checkPreload(@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
                                                                                   @Valid LoanPreloadRequest loanPreloadRequest) {
        TmbOneServiceResponse<LoanPreloadResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {

            oneTmbOneServiceResponse.setData(personalLoanService.checkPreload(correlationId, loanPreloadRequest));
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while getConfig: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }


    }

    @GetMapping(value = "/get-preload-brms", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get preload brms")
    public ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> checkCalUW(@Valid InstantLoanCalUWRequest instantLoanCalUWRequest) {
        TmbOneServiceResponse<InstantLoanCalUWResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {
            InstantLoanCalUWResponse instantLoanCalUWResponse = loanCalUWService.checkCalculateUnderwriting(instantLoanCalUWRequest);
            oneTmbOneServiceResponse.setData(instantLoanCalUWResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while check under writing: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }


    @GetMapping(value = "/get-product-loan-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product loan list")
    public ResponseEntity<TmbOneServiceResponse<ApplyPersonalLoan>> getProductList() {
        TmbOneServiceResponse<ApplyPersonalLoan> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {
            ApplyPersonalLoan productDataList = personalLoanService.getProducts();
            oneTmbOneServiceResponse.setData(productDataList);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get product list: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @GetMapping(value = "/get-product-credit-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product credit list")
    public ResponseEntity<TmbOneServiceResponse<List<ProductData>>> getProductCreditList() {
        TmbOneServiceResponse<List<ProductData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {
            List<ProductData> productDataList = personalLoanService.getProductsCredit();
            oneTmbOneServiceResponse.setData(productDataList);
            dataSuccess(responseHeaders,oneTmbOneServiceResponse);

            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get product credit list: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    ResponseEntity<TmbOneServiceResponse<List<ProductData>>> dataSuccess(HttpHeaders responseHeaders, TmbOneServiceResponse<List<ProductData>> oneTmbOneServiceResponse) {
        oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
    }

}
