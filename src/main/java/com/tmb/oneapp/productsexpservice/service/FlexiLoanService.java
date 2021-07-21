package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlexiLoanService {
    private static final TMBLogger<FlexiLoanService> logger = new TMBLogger<>(FlexiLoanService.class);

    private final LendingServiceClient lendingServiceClient;

    public SubmissionInfoResponse getSubmissionInfo(String correlationId, SubmissionInfoRequest request) throws TMBCommonException {

        try {
            TmbOneServiceResponse<SubmissionInfoResponse> responseEntity = lendingServiceClient.submissionInfo(correlationId,request).getBody();
            if (responseEntity.getStatus().getCode().equals("0000")) {
                return responseEntity.getData();
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("submission info got exception:{}", e);
            throw e;
        }
    }
}
