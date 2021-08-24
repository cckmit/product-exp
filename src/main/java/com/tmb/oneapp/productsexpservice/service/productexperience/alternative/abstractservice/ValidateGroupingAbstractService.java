package com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;

public class ValidateGroupingAbstractService {

    protected final AlternativeService alternativeService;

    public ValidateGroupingAbstractService(AlternativeService alternativeService) {
        this.alternativeService = alternativeService;
    }

    protected TmbOneServiceResponse<String> validateSuitabilityExpired(String correlationId,
                                                                       String crmId,
                                                                       TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                                       TmbStatus status){
        // validate suitability expired
        tmbOneServiceResponse.setStatus(alternativeService.validateSuitabilityExpired(correlationId, crmId, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            return tmbOneServiceResponse;
        }

        return tmbOneServiceResponse;

    }

    protected TmbOneServiceResponse<String> validateServiceHourAgeAndRisk(String correlationId,
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
