package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.OneAppConfig;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonalLoanService {

    private static final TMBLogger<PersonalLoanService> logger = new TMBLogger(PersonalLoanService.class);
    private final CommonServiceClient commonServiceClient;

    public LoanPreloadResponse checkPreload(LoanPreloadRequest loanPreloadRequest) {

        LoanPreloadResponse loanPreloadResponse = new LoanPreloadResponse();

        TmbOneServiceResponse<OneAppConfig> configs = getAllConfig(loanPreloadRequest.getChannel());

        loanPreloadResponse.setFlagePreload(checkPreloadConfig(configs));

        return loanPreloadResponse;
    }

    public TmbOneServiceResponse<OneAppConfig> getAllConfig(String channel) {
        TmbOneServiceResponse<OneAppConfig> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<OneAppConfig>> nodeTextResponse = commonServiceClient.getAllConfig(channel);
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());

        } catch (Exception e) {
            logger.error("get all config fail: ", e);

        }

        return oneTmbOneServiceResponse;
    }

    public Boolean checkPreloadConfig(TmbOneServiceResponse<OneAppConfig> configs) {

        // check config
        return true;
    }
}
