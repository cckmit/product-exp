package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.service.LoanCalculatorService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-submission")
@Api(tags = "LoanSubmission Service")
public class LoanSubmissionCustomerController {
    private final LoanSubmissionCustomerService loanSubmissionCustomerService;
    private final LoanCalculatorService loanCalculatorService;

    private static final TMBLogger<LoanSubmissionCustomerController> logger =
            new TMBLogger<>(LoanSubmissionCustomerController.class);


    @GetMapping("/get-data-income-info")
    @LogAround
    @ApiOperation("Get customer info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header")})
    public ResponseEntity<TmbOneServiceResponse<LoanSubmissionResponse>> getIncomeInfo(@Valid @RequestHeader(name = X_CRMID) String crmId,
                                                                                       @Valid @RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId
    ) {
        TmbOneServiceResponse<LoanSubmissionResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {

            LoanSubmissionResponse loanSubmissionResponse = loanSubmissionCustomerService.getCustomerInfo(correlationId, crmId);
            oneTmbOneServiceResponse.setData(loanSubmissionResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while getIncomeInfo: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }


    @GetMapping("/getPreloadLoanCalculator")
    @LogAround
    @ApiOperation("Get Preload Loan Calculator")
    public ResponseEntity<TmbOneServiceResponse<LoanCalculatorResponse>> getPreloadLoanCal(@Valid LoanCalculatorRequest request) {
        TmbOneServiceResponse<LoanCalculatorResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        try {

            LoanCalculatorResponse loanCalculatorResponse = loanCalculatorService.getPreloadLoanCal(request);
            oneTmbOneServiceResponse.setData(loanCalculatorResponse);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while getPreload loan cal: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }

    }
}
