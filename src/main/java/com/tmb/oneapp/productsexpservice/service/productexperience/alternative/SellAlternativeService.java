package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
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

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);

            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);

            // validate service hour
            tmbOneServiceResponse.setStatus(alternativeService.validateServiceHour(correlationId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
                return tmbOneServiceResponse;
            }

            // validate age should > 20
            tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
                return tmbOneServiceResponse;
            }

            // validate customer risk level
            tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId,customerInfo, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
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
