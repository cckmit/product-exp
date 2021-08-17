package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.stereotype.Service;

@Service
public class SwitchAlternativeService extends SellAndSwitchAbstractService {

    public SwitchAlternativeService(AlternativeService alternativeService, CustomerService customerService) {
        super(alternativeService, customerService);
    }

    private static final TMBLogger<SwitchAlternativeService> logger = new TMBLogger<>(SwitchAlternativeService.class);

    public TmbOneServiceResponse<String> validationSwitch(String correlationId, String crmId) {

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);
            return validateSellAndSwitch(correlationId,crmId,customerInfo,tmbOneServiceResponse,status);

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

}
