package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.personaldetail.*;
import com.tmb.oneapp.productsexpservice.model.request.lending.EAppRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.*;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loanSubmissionOnline")
@Api(tags = "Loan Submission Online")
public class LoanSubmissionOnlineController {
    private final LoanSubmissionOnlineService loanSubmissionOnlineService;
    private static final TMBLogger<LoanSubmissionOnlineController> logger = new TMBLogger<>(LoanSubmissionOnlineController.class);

    @GetMapping("/getIncomeInfo")
    @LogAround
    @ApiOperation(value = "get income info")

    public ResponseEntity<TmbOneServiceResponse<IncomeInfo>> getIncomeInfo(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<IncomeInfo> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            oneTmbOneServiceResponse.setData(loanSubmissionOnlineService.getIncomeInfoByRmId(crmId));
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while get income info: {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }
    }

    @LogAround
    @ApiOperation("Submission Create application")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> createApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
                                                                                        @Valid @RequestBody LoanSubmissionCreateApplicationReq request) {

        TmbOneServiceResponse<ResponseApplication> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ResponseApplication res = loanSubmissionOnlineService.createApplication(crmId, request);
            oneTmbOneServiceResponse.setData(res);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while submission create application : {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }
    }

    @GetMapping(value = "/personalDetail")
    @LogAround
    @ApiOperation("Get Personal Detail")
    public ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> getPersonalDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid PersonalDetailRequest request) {
        TmbOneServiceResponse<PersonalDetailResponse> response = new TmbOneServiceResponse<>();
        try {
            PersonalDetailResponse personalDetailResponse = loanSubmissionOnlineService.getPersonalDetailInfo(crmId, request);
            response.setData(personalDetailResponse);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("error while get personal detail: {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @PostMapping(value = "/savePersonalDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Update Personal Detail")
    public ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> savePersonalDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody PersonalDetailSaveInfoRequest request) {
        TmbOneServiceResponse<PersonalDetailResponse> response = new TmbOneServiceResponse<>();
        try {
            PersonalDetailResponse personalDetailResponse = loanSubmissionOnlineService.updatePersonalDetailInfo(crmId, request);
            response.setData(personalDetailResponse);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);
        } catch (Exception e) {
            logger.error("error while update personal customer detail: {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
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
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error while get dropdown loan submission online working detail : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
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
            response.setStatus(TmbStatusUtil.successStatus());

            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error while get loan submission online working detail : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @LogAround
    @ApiOperation("Update working detail")
    @PutMapping(value = "/workingDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateWorkingDetail(@Valid @RequestBody UpdateWorkingDetailReq request) {
        TmbOneServiceResponse<ResponseApplication> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ResponseApplication res = loanSubmissionOnlineService.updateWorkingDetail(request);
            oneTmbOneServiceResponse.setData(res);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("Error while update working detail : {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
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
            CustomerInformationResponse customerInfoRes = loanSubmissionOnlineService
                    .getCustomerInformation(correlationId, crmId, request);
            response.setData(customerInfoRes);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
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
            CustomerInformationResponse customerInfoRes = loanSubmissionOnlineService
                    .updateNCBConsentFlagAndStoreFile(correlationId, crmId, request);
            response.setData(customerInfoRes);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }


    @GetMapping("/verifyCustomer")
    @LogAround
    @ApiOperation(value = "get customer age")
    public ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> getCustomerAge(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {

            oneTmbOneServiceResponse.setData(loanSubmissionOnlineService.getCustomerAge(crmId));
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);

        } catch (Exception e) {
            logger.error("Error while get customer age: {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }
    }

    @GetMapping(value = "/documents")
    @LogAround
    @ApiOperation("Checklist Document")
    public ResponseEntity<TmbOneServiceResponse<DocumentResponse>> getDocuments(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid ChecklistRequest request) {
        TmbOneServiceResponse<DocumentResponse> response = new TmbOneServiceResponse<>();
        try {
            DocumentResponse documentResponse = loanSubmissionOnlineService.getDocuments(correlationId, crmId, request.getCaId());
            response.setData(documentResponse);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("error while get checklist : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @GetMapping(value = "/documents/more")
    @LogAround
    @ApiOperation("Checklist More Document")
    public ResponseEntity<TmbOneServiceResponse<DocumentResponse>> getMoreDocuments(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid ChecklistRequest request) {
        TmbOneServiceResponse<DocumentResponse> response = new TmbOneServiceResponse<>();
        try {
            DocumentResponse documentResponse = loanSubmissionOnlineService.getMoreDocuments(correlationId, crmId, request.getCaId());
            response.setData(documentResponse);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("error while get checklist : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @GetMapping(value = "/e-app")
    @LogAround
    @ApiOperation("Get E-App Data")
    public ResponseEntity<TmbOneServiceResponse<EAppResponse>> getEAppData(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid EAppRequest request) {
        TmbOneServiceResponse<EAppResponse> response = new TmbOneServiceResponse<>();
        try {
            EAppResponse eAppResponses = loanSubmissionOnlineService.getEAppData(correlationId, crmId, request.getCaId());
            response.setData(eAppResponses);
            response.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

        } catch (Exception e) {
            logger.error("error while get e-app : {}", e);
            response.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

    @LogAround
    @ApiOperation("Update Application")
    @PutMapping(value = "updateApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
                                                                                        @Valid @RequestBody LoanSubmissionCreateApplicationReq request) {

        TmbOneServiceResponse<ResponseApplication> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            loanSubmissionOnlineService.updateApplication(crmId, request);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        } catch (Exception e) {

            logger.error("Error while update application : {}", e);
            oneTmbOneServiceResponse.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneTmbOneServiceResponse);
        }
    }

}
