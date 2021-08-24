package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice.SellAndSwitchAbstractService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.stereotype.Service;

@Service
public class SellAlternativeService extends SellAndSwitchAbstractService {

    private static final TMBLogger<SellAlternativeService> logger = new TMBLogger<>(SellAlternativeService.class);

    public SellAlternativeService(AlternativeService alternativeService, CustomerService customerService) {
        super(alternativeService, customerService);
    }

    public TmbOneServiceResponse<String> validationSell(String correlationId, String crmId) {

        TmbOneServiceResponse<String> tmbOneServicesResponse = new TmbOneServiceResponse();
        try {

            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServicesResponse.setStatus(status);

            tmbOneServicesResponse = validateSellAndSwitch(correlationId,customerInfo,tmbOneServicesResponse,status);
            if(!tmbOneServicesResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return tmbOneServicesResponse;
            }

            // validate suitability expired
            tmbOneServicesResponse = validateSuitabilityExpired(correlationId,crmId,tmbOneServicesResponse,status);
            if (!tmbOneServicesResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServicesResponse;
            }

            return tmbOneServicesResponse;

        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            tmbOneServicesResponse.setStatus(null);
            tmbOneServicesResponse.setData(null);
            return tmbOneServicesResponse;
        }
    }

}
