package com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;

public abstract class BuyAndDcaAbstractService extends ValidateGroupingAbstractService {

    protected final AlternativeService alternativeService;

    protected final CustomerService customerService;

    protected final ProductsExpService productsExpService;

    protected final InvestmentRequestClient investmentRequestClient;


    public BuyAndDcaAbstractService(AlternativeService alternativeService,
                                    CustomerService customerService,
                                    ProductsExpService productsExpService,
                                    InvestmentRequestClient investmentRequestClient) {
        super(alternativeService);
        this.alternativeService = alternativeService;
        this.customerService = customerService;
        this.productsExpService = productsExpService;
        this.investmentRequestClient = investmentRequestClient;
    }

    protected TmbOneServiceResponse<String> validateBuyAndDca(String correlationId,
                                                              String crmId,
                                                              CustomerSearchResponse customerInfo,
                                                              String processFlag,
                                                              TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                              TmbStatus status){

        // process flag != Y = Can'y By fund
        if(!ProductsExpServiceConstant.PROCESS_FLAG_Y.equals(processFlag)){
            status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getCode());
            status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getDesc());
            status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        tmbOneServiceResponse = validateServiceHourAgeAndRisk(correlationId,customerInfo,tmbOneServiceResponse,status);
        if(!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
            return tmbOneServiceResponse;
        }

        // validate casa dormant
        tmbOneServiceResponse.setStatus(alternativeService.validateCASADormant(correlationId, crmId, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            return tmbOneServiceResponse;
        }

        // validate id card expired
        tmbOneServiceResponse.setStatus(alternativeService.validateIdCardExpired( crmId, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
            return tmbOneServiceResponse;
        }

        // validate flatca flag not valid
        tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid( customerInfo.getFatcaFlag(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
            return tmbOneServiceResponse;
        }

        return tmbOneServiceResponse;
    }
}
