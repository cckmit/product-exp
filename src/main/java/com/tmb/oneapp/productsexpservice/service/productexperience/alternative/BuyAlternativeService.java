package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.stereotype.Service;

@Service
public class BuyAlternativeService extends BuyAndDcaAbstractService {

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    public BuyAlternativeService(AlternativeService alternativeService, CustomerService customerService, ProductsExpService productsExpService, InvestmentRequestClient investmentRequestClient) {
        super(alternativeService, customerService, productsExpService, investmentRequestClient);
    }

    public TmbOneServiceResponse<String> validationBuy(String correlationId, String crmId, AlternativeBuyRequest alternativeBuyRequest) {

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);

            String trackingId = ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING;
            productsExpService.logActivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                    crmId,
                    ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                    trackingId,
                    alternativeBuyRequest));
            String processFlag = alternativeBuyRequest.getProcessFlag();

            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);
            tmbOneServiceResponse = validateBuyAndDca(correlationId,crmId,customerInfo,processFlag,tmbOneServiceResponse,status);
            if(!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return tmbOneServiceResponse;
            }

            // validate suitability expired
            tmbOneServiceResponse.setStatus(alternativeService.validateSuitabilityExpired(correlationId, crmId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            return tmbOneServiceResponse;

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

}
