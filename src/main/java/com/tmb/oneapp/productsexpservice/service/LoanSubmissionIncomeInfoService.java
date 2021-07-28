package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanSubmissionIncomeInfoService {
    private static final TMBLogger<LoanSubmissionIncomeInfoService> logger = new TMBLogger<>(LoanSubmissionIncomeInfoService.class);
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
}
