package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
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

import java.util.List;

@Service
@AllArgsConstructor
public class LoanSubmissionOnlineService {
    private static final TMBLogger<LoanSubmissionOnlineService> logger = new TMBLogger<>(LoanSubmissionOnlineService.class);
    private final LendingServiceClient lendingServiceClient;

    public IncomeInfo getIncomeInfoByRmId(String rmId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<IncomeInfo> responseEntity = lendingServiceClient.getIncomeInfo(rmId).getBody();
            if (responseEntity.getStatus().getCode().equals("0000")) {
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
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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
            if (response.getBody().getData().getHeader().getResponseCode().equals("MSG_000")) {
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

            if (responseEntity.getStatus().getCode().equals("0000")) {
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
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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

    public DropdownsLoanSubmissionWorkingDetail getDropdownsLoanSubmissionWorkingDetail(String correlationId, String crmId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> responseEntity = lendingServiceClient.getDropdownLoanSubmissionWorkingDetail(correlationId, crmId).getBody();
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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
            if (response.getBody().getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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

    public List<ChecklistResponse> getDocuments(String crmId, Long caId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<List<ChecklistResponse>> responseEntity = lendingServiceClient.getDocuments(crmId, caId).getBody();

            if (responseEntity.getStatus().getCode().equals("0000")) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get checklist document got exception:{}", e);
            throw e;
        }
    }

    public CustomerInformationResponse updateNCBConsentFlagAndStoreFile(String correlationId, String crmId, UpdateNCBConsentFlagRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<CustomerInformationResponse> responseEntity = lendingServiceClient
                    .updateNCBConsentFlagAndStoreFile(correlationId, crmId, request).getBody();
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
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
            if (responseEntity.getStatus().getCode().equals("0000")) {
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

            if (responseEntity.getStatus().getCode().equals("0000")) {
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

}
