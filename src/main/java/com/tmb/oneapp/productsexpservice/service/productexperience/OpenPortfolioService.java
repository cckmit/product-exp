package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * OpenPortfolioService class will get data from api services, and handle business criteria
 */
@Service
public class OpenPortfolioService {

    private static final TMBLogger<ProductsExpService> logger = new TMBLogger<>(ProductsExpService.class);

    private CommonServiceClient commonServiceClient;

    @Autowired
    public OpenPortfolioService(CommonServiceClient commonServiceClient) {
        this.commonServiceClient = commonServiceClient;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioRequest
     */
    public ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> validateOpenPortfolio(String correlationId, OpenPortfolioRequest openPortfolioRequest) {
        return commonServiceClient.getTermAndConditionByServiceCodeAndChannel(correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
    }
}
