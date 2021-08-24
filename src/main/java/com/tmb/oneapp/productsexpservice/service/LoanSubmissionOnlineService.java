package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.LoanSubmissionGetCustomerAgeResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("getIncomeInfoByRmId got exception:{}", e);
            throw e;
        }
    }

    public DropdownsLoanSubmissionWorkingDetail getDropdownsLoanSubmissionWorkingDetail(String correlationId, String crmId) throws TMBCommonException {
        try {
            TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> responseEntity = lendingServiceClient.getDropdownLoanSubmissionWorkingDetail(correlationId, crmId).getBody();
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
                return responseEntity.getData();
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
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
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("getLoanSubmissionWorkingDetail got exception:{}", e);
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
}
