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
        TmbOneServiceResponse<IncomeInfo> getIncomeInfoResp = new TmbOneServiceResponse<>();
        try {
            getIncomeInfoResp.setData(loanSubmissionOnlineService.getIncomeInfoByRmId(crmId));
            getIncomeInfoResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(getIncomeInfoResp);

        } catch (Exception e) {
            logger.error("Error while get income info: {}", e);
            getIncomeInfoResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(getIncomeInfoResp);
        }
    }

    @LogAround
    @ApiOperation("Submission Create application")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> createApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
                                                                                        @Valid @RequestBody LoanSubmissionCreateApplicationReq request) {

        TmbOneServiceResponse<ResponseApplication> createApplicationResp = new TmbOneServiceResponse<>();
        try {
            ResponseApplication res = loanSubmissionOnlineService.createApplication(crmId, request);
            createApplicationResp.setData(res);
            createApplicationResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(createApplicationResp);

        } catch (Exception e) {
            logger.error("Error while submission create application : {}", e);
            createApplicationResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(createApplicationResp);
        }
    }

    @GetMapping(value = "/personalDetail")
    @LogAround
    @ApiOperation("Get Personal Detail")
    public ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> getPersonalDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid PersonalDetailRequest request) {
        TmbOneServiceResponse<PersonalDetailResponse> getPersonalDetailResp = new TmbOneServiceResponse<>();
        try {
            PersonalDetailResponse personalDetailResponse = loanSubmissionOnlineService.getPersonalDetailInfo(crmId, request);
            getPersonalDetailResp.setData(personalDetailResponse);
            getPersonalDetailResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(getPersonalDetailResp);

        } catch (Exception e) {
            logger.error("error while get personal detail: {}", e);
            getPersonalDetailResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(getPersonalDetailResp);
        }
    }

    @PostMapping(value = "/savePersonalDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Update Personal Detail")
    public ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> savePersonalDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @RequestBody PersonalDetailSaveInfoRequest request) {
        TmbOneServiceResponse<PersonalDetailResponse> savePersonalDetailResp = new TmbOneServiceResponse<>();
        try {
            PersonalDetailResponse personalDetailResponse = loanSubmissionOnlineService.updatePersonalDetailInfo(crmId, request);
            savePersonalDetailResp.setData(personalDetailResponse);
            savePersonalDetailResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(savePersonalDetailResp);
        } catch (Exception e) {
            logger.error("error while update personal customer detail: {}", e);
            savePersonalDetailResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(savePersonalDetailResp);
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
        TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> dropdownWorkingDetailResp = new TmbOneServiceResponse<>();

        try {
            DropdownsLoanSubmissionWorkingDetail dropdownsLoanSubmissionWorkingDetail = loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail(correlationId, crmId);
            dropdownWorkingDetailResp.setData(dropdownsLoanSubmissionWorkingDetail);
            dropdownWorkingDetailResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(dropdownWorkingDetailResp);

        } catch (Exception e) {
            logger.error("Error while get dropdown loan submission online working detail : {}", e);
            dropdownWorkingDetailResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(dropdownWorkingDetailResp);
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
        TmbOneServiceResponse<WorkingDetail> workingDetailResp = new TmbOneServiceResponse<>();

        try {
            WorkingDetail workingDetail = loanSubmissionOnlineService.getWorkingDetail(correlationId, crmId, caId);
            workingDetailResp.setData(workingDetail);
            workingDetailResp.setStatus(TmbStatusUtil.successStatus());

            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(workingDetailResp);

        } catch (Exception e) {
            logger.error("Error while get loan submission online working detail : {}", e);
            workingDetailResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(workingDetailResp);
        }
    }

    @LogAround
    @ApiOperation("Update working detail")
    @PutMapping(value = "/workingDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateWorkingDetail(@Valid @RequestBody UpdateWorkingDetailReq request) {
        TmbOneServiceResponse<ResponseApplication> updateWorkingDetailResp = new TmbOneServiceResponse<>();
        try {
            ResponseApplication res = loanSubmissionOnlineService.updateWorkingDetail(request);
            updateWorkingDetailResp.setData(res);
            updateWorkingDetailResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(updateWorkingDetailResp);
        } catch (Exception e) {
            logger.error("Error while update working detail : {}", e);
            updateWorkingDetailResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(updateWorkingDetailResp);
        }
    }

    @ApiOperation("Get Loan Submission Customer Information")
    @PostMapping(value = "/get-customer-information", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> getCustomerInformation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody UpdateNCBConsentFlagRequest request) {
        TmbOneServiceResponse<CustomerInformationResponse> customerInfoResp = new TmbOneServiceResponse<>();

        try {
            CustomerInformationResponse customerInfoRes = loanSubmissionOnlineService
                    .getCustomerInformation(correlationId, crmId, request);
            customerInfoResp.setData(customerInfoRes);
            customerInfoResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(customerInfoResp);

        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            customerInfoResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(customerInfoResp);
        }
    }

    @ApiOperation("Loan Submission Update NCB consent flag and store file to sFTP")
    @PostMapping(value = "/update-flag-and-store-ncb-consent", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    public ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> updateNCBConsentFlagAndStoreFile(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @ApiParam(value = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody UpdateNCBConsentFlagRequest request) {
        TmbOneServiceResponse<CustomerInformationResponse> updateNCBConsentResp = new TmbOneServiceResponse<>();
        try {
            CustomerInformationResponse customerInfoRes = loanSubmissionOnlineService
                    .updateNCBConsentFlagAndStoreFile(correlationId, crmId, request);
            updateNCBConsentResp.setData(customerInfoRes);
            updateNCBConsentResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(updateNCBConsentResp);

        } catch (Exception e) {
            logger.error("Error while get loan submission Customer Information : {}", e);
            updateNCBConsentResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(updateNCBConsentResp);
        }
    }


    @GetMapping("/verifyCustomer")
    @LogAround
    @ApiOperation(value = "get customer age")
    public ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> getCustomerAge(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse> customerAgeResp = new TmbOneServiceResponse<>();
        try {

            customerAgeResp.setData(loanSubmissionOnlineService.getCustomerAge(crmId));
            customerAgeResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(customerAgeResp);

        } catch (Exception e) {
            logger.error("Error while get customer age: {}", e);
            customerAgeResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(customerAgeResp);
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
        TmbOneServiceResponse<DocumentResponse> documentResp = new TmbOneServiceResponse<>();
        try {
            DocumentResponse documentResponse = loanSubmissionOnlineService.getDocuments(correlationId, crmId, request.getCaId());
            documentResp.setData(documentResponse);
            documentResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(documentResp);

        } catch (Exception e) {
            logger.error("error while get checklist : {}", e);
            documentResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(documentResp);
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
        TmbOneServiceResponse<DocumentResponse> moreDocumentResp = new TmbOneServiceResponse<>();
        try {
            DocumentResponse documentResponse = loanSubmissionOnlineService.getMoreDocuments(correlationId, crmId, request.getCaId());
            moreDocumentResp.setData(documentResponse);
            moreDocumentResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(moreDocumentResp);

        } catch (Exception e) {
            logger.error("error while get checklist : {}", e);
            moreDocumentResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(moreDocumentResp);
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
        TmbOneServiceResponse<EAppResponse> eappResp = new TmbOneServiceResponse<>();
        try {
            EAppResponse eAppResponses = loanSubmissionOnlineService.getEAppData(correlationId, crmId, request.getCaId());
            eappResp.setData(eAppResponses);
            eappResp.setStatus(TmbStatusUtil.successStatus());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(eappResp);

        } catch (Exception e) {
            logger.error("error while get e-app : {}", e);
            eappResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(eappResp);
        }
    }

    @LogAround
    @ApiOperation("Update Application")
    @PutMapping(value = "updateApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ResponseApplication>> updateApplication(@RequestHeader(name = ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
                                                                                        @Valid @RequestBody LoanSubmissionCreateApplicationReq request) {

        TmbOneServiceResponse<ResponseApplication> updateApplicationResp = new TmbOneServiceResponse<>();
        try {
            loanSubmissionOnlineService.updateApplication(crmId, request);
            updateApplicationResp.setStatus(TmbStatusUtil.successStatus());

            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(updateApplicationResp);
        } catch (Exception e) {

            logger.error("Error while update application : {}", e);
            updateApplicationResp.setStatus(TmbStatusUtil.failedStatus());
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(updateApplicationResp);
        }
    }

}
