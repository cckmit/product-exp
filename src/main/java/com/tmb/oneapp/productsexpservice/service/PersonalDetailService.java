package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.DropDown;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PersonalDetailService {

    private static final TMBLogger<LoanSubmissionInstantLoanCalUWService> logger = new TMBLogger<>(LoanSubmissionInstantLoanCalUWService.class);
    private final LendingServiceClient lendingServiceClient;

    public PersonalDetailResponse getPersonalDetailInfo(String crmid, PersonalDetailRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<PersonalDetailResponse> responseEntity = lendingServiceClient.getPersonalDetail(crmid,request.getCaId()).getBody();

            if (responseEntity.getStatus().getCode().equals("0000")) {
                return responseEntity.getData();
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("get personal detail got exception:{}", e);
            throw e;
        }
    }
}
