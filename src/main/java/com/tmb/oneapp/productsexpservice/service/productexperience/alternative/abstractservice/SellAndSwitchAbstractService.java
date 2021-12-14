package com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;

public abstract class SellAndSwitchAbstractService extends ValidateGroupingAbstractService {

    protected final CustomerService customerService;

    protected SellAndSwitchAbstractService(AlternativeService alternativeService,
                                           CustomerService customerService) {
        super(alternativeService);
        this.customerService = customerService;
    }

    protected TmbOneServiceResponse<String> validateSellAndSwitch(String correlationId,
                                                                  CustomerSearchResponse customerInfo,
                                                                  TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                                  TmbStatus status) {

        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(false).build();
        return validateGroupingService(correlationId, customerInfo, tmbOneServiceResponse,
                status, buyFlowFirstTrade);
    }
}
