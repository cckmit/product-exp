package com.tmb.oneapp.productsexpservice.controller;


import java.time.Instant;

import javax.validation.Valid;

import com.tmb.oneapp.productsexpservice.model.response.lending.LoanSubmissionGetCustomerAgeResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInformationResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.UpdateNCBConsentFlagRequest;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCreateApplicationService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionGetCustomerInformationService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionUpdateNCBConsentFlagAndStoreFileService;
import com.tmb.oneapp.productsexpservice.service.WorkingDetailUpdateInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loanSubmissionOnline")
@Api(tags = "waive income")
public class LoanSubmissionOnlineController {
    private final LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;
    private final LoanSubmissionOnlineService loanSubmissionOnlineService;
    private final LoanSubmissionGetCustomerInformationService loanSubmissionGetCustInfoAppInfoService;
    private final LoanSubmissionUpdateNCBConsentFlagAndStoreFileService loanSubmissionUpdateNCBConsentFlagAndStoreFileService;
    private final WorkingDetailUpdateInfoService workingDetailUpdateInfoService;
    private static final TMBLogger<LoanSubmissionOnlineController> logger = new TMBLogger<>(LoanSubmissionOnlineController.class);

    private String timeStamp = "Timestamp";

    @GetMapping("/getIncomeInfo")
    @LogAround
    @ApiOperation(value = "get income info")

    public ResponseEntity<TmbOneServiceResponse<IncomeInfo>> getIncomeInfo(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<IncomeInfo> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {

            oneTmbOneServiceResponse.setData(loanSubmissionOnlineService.getIncomeInfoByRmId(crmId));
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set(timeStamp, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("Error while get income info: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @LogAround
    @ApiOperation("Submission Create application")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> createApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
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

            responseHeaders.set(timeStamp, String.valueOf(Instant.now().toEpochMilli()));
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

    @ApiOperation("Get Loan Submission Working Detail")
    @GetMapping(value = "/workingDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<WorkingDetail>> getWorkingDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestParam(value = "caId") Long caId
    ) {
        TmbOneServiceResponse<WorkingDetail> response = new TmbOneServiceResponse<>();

        try {
            WorkingDetail workingDetail = loanSubmissionOnlineService.getWorkingDetail(correlationId, crmId, caId);
            response.setData(workingDetail);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

            return ResponseEntity.ok()
                    .headers(TMBUtils.getResponseHeaders())
                    .body(response);

        } catch (Exception e) {
            logger.error("Error while get loan submission online working detail : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @LogAround
    @ApiOperation("Update working detail")
    @PutMapping(value = "/workingDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateWorkingDetail(@Valid @RequestBody UpdateWorkingDetailReq request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<ResponseApplication> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ResponseApplication res = workingDetailUpdateInfoService.updateWorkingDetail(request);
            oneTmbOneServiceResponse.setData(res);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set(timeStamp, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("Error while update working detail : {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @ApiOperation("Get Loan Submission Customer Information")
    @PostMapping(value = "/get-customer-information", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> getCustomerInformation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody UpdateNCBConsentFlagRequest request) {
        TmbOneServiceResponse<CustomerInformationResponse> response = new TmbOneServiceResponse<>();

        try {
            CustomerInformationResponse customerInfoRes = loanSubmissionGetCustInfoAppInfoService
                    .getCustomerInformation(correlationId, crmId, request);
            response.setData(customerInfoRes);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @ApiOperation("Loan Submission Update NCB consent flag and store file to sFTP")
    @PostMapping(value = "/update-flag-and-store-ncb-consent", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> updateNCBConsentFlagAndStoreFile(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody UpdateNCBConsentFlagRequest request) {
        TmbOneServiceResponse<CustomerInformationResponse> response = new TmbOneServiceResponse<>();
        try {
            CustomerInformationResponse customerInfoRes = loanSubmissionUpdateNCBConsentFlagAndStoreFileService
                    .updateNCBConsentFlagAndStoreFile(correlationId, crmId, request);
            response.setData(customerInfoRes);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);
        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }


    @GetMapping("/getCustomerAge")
    @LogAround
    @ApiOperation(value = "get customer age")
    public ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> getCustomerAge(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {

            oneTmbOneServiceResponse.setData(loanSubmissionOnlineService.getCustomerAge(crmId));
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseHeaders.set(timeStamp, String.valueOf(Instant.now().toEpochMilli()));
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("Error while get customer age: {}", e);
            oneTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

}
