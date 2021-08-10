package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanSubmissionOnlineService {
    private static final TMBLogger<LoanSubmissionOnlineService> logger = new TMBLogger<>(LoanSubmissionOnlineService.class);
    private final LendingServiceClient lendingServiceClient;

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
}
