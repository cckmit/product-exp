package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ApplyPersonalLoan;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lending")
@Api(tags = "Personal Loan")
public class PersonalLoanController {

    private final PersonalLoanService personalLoanService;
    private final LoanSubmissionOnlineService loanSubmissionOnlineService;
    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);

    @GetMapping("/get_preload")
    @LogAround
    @ApiOperation(value = "check flag preload from config mongodb")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> checkPreload(@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
                                                                                   @Valid LoanPreloadRequest loanPreloadRequest) {
        TmbOneServiceResponse<LoanPreloadResponse> checkPreloadResp = new TmbOneServiceResponse<>();

        try {
            checkPreloadResp.setData(personalLoanService.checkPreload(correlationId, loanPreloadRequest));
            checkPreloadResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(checkPreloadResp);
        } catch (Exception e) {
            logger.error("error while getConfig: {}", e);
            checkPreloadResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(checkPreloadResp);
        }
    }

    @GetMapping(value = "/get-preload-brms", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get preload brms")
    public ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> checkCalUW(@Valid InstantLoanCalUWRequest instantLoanCalUWRequest) {
        TmbOneServiceResponse<InstantLoanCalUWResponse> checkCalUWResp = new TmbOneServiceResponse<>();
        try {
            InstantLoanCalUWResponse instantLoanCalUWResponse = loanSubmissionOnlineService.checkCalculateUnderwriting(instantLoanCalUWRequest);
            checkCalUWResp.setData(instantLoanCalUWResponse);
            checkCalUWResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(checkCalUWResp);
        } catch (Exception e) {
            logger.error("error while check under writing: {}", e);
            checkCalUWResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(checkCalUWResp);
        }
    }


    @GetMapping(value = "/get-product-loan-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product loan list")
    public ResponseEntity<TmbOneServiceResponse<ApplyPersonalLoan>> getProductList() {
        TmbOneServiceResponse<ApplyPersonalLoan> productListResp = new TmbOneServiceResponse<>();

        try {
            ApplyPersonalLoan productDataLoanList = personalLoanService.getProductsLoan();
            productListResp.setData(productDataLoanList);
            productListResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(productListResp);
        } catch (Exception e) {
            logger.error("error while get product list: {}", e);
            productListResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(productListResp);
        }
    }


    @GetMapping(value = "/get-product-credit-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Get product credit list")
    public ResponseEntity<TmbOneServiceResponse<List<ProductData>>> getProductCreditList() {
        TmbOneServiceResponse<List<ProductData>> productCreditListResp = new TmbOneServiceResponse<>();
        try {
            List<ProductData> productDataCreditList = personalLoanService.getProductsCredit();
            productCreditListResp.setData(productDataCreditList);
            productCreditListResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().body(productCreditListResp);
        } catch (Exception e) {
            logger.error("error while get product credit list: {}", e);
            productCreditListResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(productCreditListResp);
        }
    }

}
