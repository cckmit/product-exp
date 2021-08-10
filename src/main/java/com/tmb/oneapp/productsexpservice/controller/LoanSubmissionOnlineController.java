package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCreateApplicationService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionIncomeInfoService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loanSubmissionOnline")
@Api(tags = "waive income")
public class LoanSubmissionOnlineController {
    private final LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;
    private final LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;
    private final LoanSubmissionOnlineService loanSubmissionOnlineService;
    private static final TMBLogger<LoanSubmissionOnlineController> logger = new TMBLogger<>(LoanSubmissionOnlineController.class);

    @GetMapping("/getIncomeInfo")
    @LogAround
    @ApiOperation(value = "get income info")

    public ResponseEntity<TmbOneServiceResponse<IncomeInfo>> getIncomeInfo(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID, required = true) String crmId) {
        TmbOneServiceResponse<IncomeInfo> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {

            oneTmbOneServiceResponse.setData(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(crmId));
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("Error while getConfig: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @LogAround
    @ApiOperation("Submission Create application")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> createApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID, required = true) String crmId,
                                                                                        @Valid @RequestBody LoanSubmissionCreateApplicationReq request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<ResponseApplication> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseApplication res = loanSubmissionCreateApplicationService.createApplication(crmId, request);
            oneTmbOneServiceResponse.setData(res);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {

            logger.error("Error while submission create application : {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @ApiOperation("Get DropdownsLoanSubmissionWorkingDetail Loan Submission Working Detail")
    @GetMapping(value = "/dropdown/workingDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail>> getDropdownLoanSubmissionWorkingDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId
    ) {
        TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> response = new TmbOneServiceResponse<>();

        try {
            DropdownsLoanSubmissionWorkingDetail dropdownsLoanSubmissionWorkingDetail = loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail(correlationId, crmId);
            response.setData(dropdownsLoanSubmissionWorkingDetail);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

            return ResponseEntity.ok()
                    .headers(TMBUtils.getResponseHeaders())
                    .body(response);

        } catch (Exception e) {
            logger.error("Error while get dropdown loan submission online working detail : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }
}
