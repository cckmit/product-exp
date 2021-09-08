package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanCalculatorService {
    private static final TMBLogger<LoanCalculatorService> logger = new TMBLogger<>(LoanCalculatorService.class);
    private final LendingServiceClient lendingServiceClient;

    public LoanCalculatorResponse getPreloadLoanCal(LoanCalculatorRequest request) throws TMBCommonException {
        try {
            TmbOneServiceResponse<LoanCalculatorResponse> responseEntity = lendingServiceClient.getPreloadLoanCalculator(request.getCaId(), request.getProduct()).getBody();

            if (responseEntity.getStatus().getCode().equals("0000")) {
                return responseEntity.getData();
            }
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            logger.error("get preload loan calculator got exception:{}", e);
            throw e;
        }
    }
}
