package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Api(tags = "Submit Generate Report")
public class ReportController {
    private final PersonalLoanService personalLoanService;
    private final NotificationService notificationService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360141010286")})
    @PostMapping(value = "/generate-report")
    public ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> generateReport(@RequestHeader Map<String, String> headers, @Valid LoanPreloadRequest loanPreloadRequest) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanPreloadResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            String accountId = headers.get(ProductsExpServiceConstant.ACCOUNT_ID);
            String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
            String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
           oneServiceResponse.setData(personalLoanService.checkPreload(correlationId, loanPreloadRequest));

            if (oneServiceResponse.getData().getFlagePreload()) {
                oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                        ProductsExpServiceConstant.SUCCESS_MESSAGE,
                        ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

                notificationService.sendCardActiveEmail(correlationId, accountId, crmId);
                return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
            } else {
                return dataNotFoundError(responseHeaders, oneServiceResponse);
            }

        }catch (Exception e) {

            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
        }
    }

    ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> dataNotFoundError(HttpHeaders responseHeaders, TmbOneServiceResponse<LoanPreloadResponse> oneServiceResponse) {
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
    }
}
