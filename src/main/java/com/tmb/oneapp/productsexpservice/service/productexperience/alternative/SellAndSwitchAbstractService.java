package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SellAndSwitchAbstractService {
    protected final AlternativeService alternativeService;

    protected final CustomerService customerService;

    @Autowired
    protected SellAndSwitchAbstractService(AlternativeService alternativeService,
                                    CustomerService customerService) {
        this.alternativeService = alternativeService;
        this.customerService = customerService;
    }

    protected TmbOneServiceResponse<String> validateSellAndSwitch(String correlationId,
                                                              CustomerSearchResponse customerInfo,
                                                              TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                              TmbStatus status){
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

        return tmbOneServiceResponse;
    }

}
