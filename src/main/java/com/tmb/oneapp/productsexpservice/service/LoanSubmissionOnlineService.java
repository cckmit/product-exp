package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.constant.RslResponseCodeEnum;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.*;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.*;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanSubmissionOnlineService {
    private static final TMBLogger<LoanSubmissionOnlineService> logger = new TMBLogger<>(LoanSubmissionOnlineService.class);
    private final LendingServiceClient lendingServiceClient;
    private final CommonServiceClient commonServiceClient;

    public IncomeInfo getIncomeInfoByRmId(String rmId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<IncomeInfo> responseEntity = lendingServiceClient.getIncomeInfo(rmId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("getIncomeInfoByRmId got exception:{}", e);
            throw e;
        }
    }

    public LoanSubmissionGetCustomerAgeResponse getCustomerAge(String crmId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse> responseEntity = lendingServiceClient.getCustomerAge(crmId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("getCustomerAge got exception:{}", e);
            throw e;
        }
    }

    public ResponseApplication createApplication(String crmId, LoanSubmissionCreateApplicationReq req) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<ResponseApplication>> response = lendingServiceClient.createApplication(crmId, req);
            if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getBody().getData().getHeader().getResponseCode())) {
                return response.getBody().getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    response.getBody().getData().getHeader().getResponseDescriptionEN(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("createApplication got exception:{}", e);
            throw e;
        }
    }

    public PersonalDetailResponse getPersonalDetailInfo(String crmid, PersonalDetailRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<PersonalDetailResponse> responseEntity = lendingServiceClient.getPersonalDetail(crmid, request.getCaId()).getBody();

            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get personal detail got exception:{}", e);
            throw e;
        }
    }

    public PersonalDetailResponse updatePersonalDetailInfo(String crmId, PersonalDetailSaveInfoRequest personalDetailSaveInfoRequest) throws TMBCommonException {
        try {
            TmbOneServiceResponse<PersonalDetailResponse> responseEntity = lendingServiceClient.saveCustomerInfo(crmId, personalDetailSaveInfoRequest).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("update customer personal detail got exception:{}", e);
            throw e;
        }
    }

    public DropdownsLoanSubmissionWorkingDetail getDropdownsLoanSubmissionWorkingDetail(String correlationId, String crmId, String caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> responseEntity = lendingServiceClient.getDropdownLoanSubmissionWorkingDetail(correlationId, crmId, caId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("getDropdownLoanSubmissionWorkingDetail got exception:{}", e);
            throw e;
        }
    }

    public WorkingDetail getWorkingDetail(String correlationId, String crmId, long caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<WorkingDetail> responseEntity = lendingServiceClient.getLoanSubmissionWorkingDetail(correlationId, crmId, caId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("getLoanSubmissionWorkingDetail got exception:{}", e);
            throw e;
        }
    }

    public ResponseApplication updateWorkingDetail(UpdateWorkingDetailReq req) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<ResponseApplication>> response = lendingServiceClient.updateWorkingDetail(req);
            if (ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
                return response.getBody().getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    response.getBody().getData().getHeader().getResponseDescriptionEN(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("updateWorkingDetail got exception:{}", e);
            throw e;
        }
    }

    public DocumentResponse getDocuments(String correlationId, String crmId, Long caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<List<ChecklistResponse>> responseEntity = lendingServiceClient.getDocuments(crmId, caId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return parseDocumentListResponse(responseEntity, correlationId);
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get checklist document got exception:{}", e);
            throw e;
        }
    }

    public DocumentResponse getMoreDocuments(String correlationId, String crmId, long caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<List<ChecklistResponse>> responseEntity = lendingServiceClient.getMoreDocuments(crmId, caId).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return parseDocumentListResponse(responseEntity, correlationId);
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get checklist more document got exception:{}", e);
            throw e;
        }
    }

    public CustomerInformationResponse updateNCBConsentFlagAndStoreFile(String correlationId, String crmId, UpdateNCBConsentFlagRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<CustomerInformationResponse> responseEntity = lendingServiceClient
                    .updateNCBConsentFlagAndStoreFile(correlationId, crmId, request).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("UpdateNCBConsentFlagAndStoreFile got exception:{}", e);
            throw e;
        }
    }


    public CustomerInformationResponse getCustomerInformation(String correlationId, String crmId,
                                                              UpdateNCBConsentFlagRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<CustomerInformationResponse> responseEntity = lendingServiceClient
                    .getCustomerInformation(correlationId, crmId, request).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("getCustomerInformation got exception:{}", e);
            throw e;
        }
    }

    public InstantLoanCalUWResponse checkCalculateUnderwriting(InstantLoanCalUWRequest request) throws TMBCommonException {

        try {
            TmbOneServiceResponse<InstantLoanCalUWResponse> responseEntity = lendingServiceClient.checkApprovedStatus(request.getCaId(), request.getTriggerFlag(), request.getProduct(), request.getLoanDay1Set()).getBody();
            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("calculateUnderwriting got exception:{}", e);
            throw e;
        }
    }


    public EAppResponse getEAppData(String correlationId, String crmId, Long caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<EAppResponse> responseEntity = lendingServiceClient.getEApp(correlationId, crmId, caId).getBody();

            if (ResponseCode.SUCESS.getCode().equals(responseEntity.getStatus().getCode())) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get e-app got exception:{}", e);
            throw e;
        }
    }

    public ResponseApplication updateApplication(String crmId, LoanSubmissionCreateApplicationReq req) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<ResponseApplication>> response = lendingServiceClient.updateApplication(crmId, req);
            if (ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
                return response.getBody().getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    response.getBody().getData().getHeader().getResponseDescriptionEN(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("createApplication got exception:{}", e);
            throw e;
        }
    }

    private DocumentResponse parseDocumentListResponse(TmbOneServiceResponse<List<ChecklistResponse>> responseEntity, String correlationId) {
        DocumentResponse documentResponse = new DocumentResponse();

        List<ChecklistResponse> responseList = new ArrayList<>();
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> commonData = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.LENDING_MODULE);

        for (ChecklistResponse checklistResponse : responseEntity.getData()) {
            ChecklistResponse response = new ChecklistResponse();
            response.setStatus(checklistResponse.getStatus());
            response.setChecklistType(checklistResponse.getChecklistType());
            response.setCifRelCode(checklistResponse.getCifRelCode());
            response.setDocId(checklistResponse.getDocId());
            response.setDocumentCode(checklistResponse.getDocumentCode());
            response.setDocDescription(checklistResponse.getDocDescription());
            response.setId(checklistResponse.getId());
            response.setIncompletedDocReasonCd(checklistResponse.getIncompletedDocReasonCd());
            response.setIncompletedDocReasonDesc(checklistResponse.getIncompletedDocReasonDesc());
            response.setLosCifId(checklistResponse.getLosCifId());
            response.setIsMandatory(checklistResponse.getIsMandatory());
            responseList.add(response);
        }

        responseEntity.setData(responseList);
        documentResponse.setChecklistResponses(responseList);
        documentResponse.setMaxPerDocType(commonData.getBody().getData().get(0).getMaxPerDoctype());
        documentResponse.setUploadFileSizeMb(commonData.getBody().getData().get(0).getUploadFileSizeMb());
        return documentResponse;
    }

}
