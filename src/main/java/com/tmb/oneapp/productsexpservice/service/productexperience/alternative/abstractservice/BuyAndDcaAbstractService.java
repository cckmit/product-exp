package com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;

/**
 * The buy and dca abstract service.
 */
public abstract class BuyAndDcaAbstractService extends ValidateGroupingAbstractService {

    protected final CustomerService customerService;

    protected final ProductsExpService productsExpService;

    protected final InvestmentRequestClient investmentRequestClient;

    protected BuyAndDcaAbstractService(AlternativeService alternativeService,
                                       CustomerService customerService,
                                       ProductsExpService productsExpService,
                                       InvestmentRequestClient investmentRequestClient) {
        super(alternativeService);
        this.customerService = customerService;
        this.productsExpService = productsExpService;
        this.investmentRequestClient = investmentRequestClient;
    }

    /**
     * Generic Method to validate process flag
     *
     * @param processFlag           the process flag
     * @param tmbOneServiceResponse the tmb one service response
     * @param status                the status
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    protected TmbOneServiceResponse<String> validateProcessFlag(String processFlag,
                                                                TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                                TmbStatus status) {
        // process flag != Y = Can not buy fund
        if (!ProductsExpServiceConstant.PROCESS_FLAG_Y.equals(processFlag)) {
            status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CAN_NOT_BUY_FUND.getCode());
            status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CAN_NOT_BUY_FUND.getDescription());
            status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CAN_NOT_BUY_FUND.getMessage());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }
        return tmbOneServiceResponse;
    }

    /**
     * Generic Method to validate buy and dca
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param customerInfo          the customer search response
     * @param tmbOneServiceResponse the tmb one service response
     * @param status                the status
     * @param isBuyFlow             the flag of buy flow
     * @param isFirstTrade          the flag of first trade
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    protected TmbOneServiceResponse<String> validateBuyAndDca(String correlationId,
                                                              String crmId,
                                                              CustomerSearchResponse customerInfo,
                                                              TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                              TmbStatus status,
                                                              boolean isBuyFlow,
                                                              boolean isFirstTrade) {

        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(isBuyFlow).isFirstTrade(isFirstTrade).build();
        tmbOneServiceResponse = validateGroupingService(correlationId, customerInfo, tmbOneServiceResponse, status, buyFlowFirstTrade);
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            return tmbOneServiceResponse;
        }

        // validate casa dormant
        tmbOneServiceResponse.setStatus(alternativeService.validateCASADormant(correlationId, crmId, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            return tmbOneServiceResponse;
        }
        return tmbOneServiceResponse;
    }
}
