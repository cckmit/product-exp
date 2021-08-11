package com.tmb.oneapp.productsexpservice.service;


import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkingDetailUpdateInfoService {
    private final LendingServiceClient lendingServiceClient;
    private static final TMBLogger<WorkingDetailUpdateInfoService> logger = new TMBLogger<>(WorkingDetailUpdateInfoService.class);


    public ResponseApplication updateWorkingDetail(UpdateWorkingDetailReq req) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<ResponseApplication>> response = lendingServiceClient.updateWorkingDetail(req);
            if (response.getBody().getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
                return response.getBody().getData();
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        response.getBody().getData().getHeader().getResponseDescriptionEN(),
                        ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error("updateWorkingDetail got exception:{}", e);
            throw e;
        }
    }
}
