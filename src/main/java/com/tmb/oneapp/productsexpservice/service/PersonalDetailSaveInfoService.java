package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalDetailSaveInfoService {

    private static final TMBLogger<PersonalDetailSaveInfoService> logger = new TMBLogger<>(PersonalDetailSaveInfoService.class);
    private final LendingServiceClient lendingServiceClient;

    public PersonalDetailResponse updatePersonalDetailInfo(String crmId, PersonalDetailSaveInfoRequest personalDetailSaveInfoRequest) throws TMBCommonException {

        try {
            TmbOneServiceResponse<PersonalDetailResponse> responseEntity = lendingServiceClient.saveCustomerInfo(crmId,personalDetailSaveInfoRequest).getBody();
            if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
                return responseEntity.getData();
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("update customer personal detail got exception:{}", e);
            throw e;
        }
    }
}
