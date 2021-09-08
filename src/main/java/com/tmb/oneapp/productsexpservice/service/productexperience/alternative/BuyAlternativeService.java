package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.buy.service.BuyActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request.FirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice.BuyAndDcaAbstractService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The buy alternative service.
 */
@Service
public class BuyAlternativeService extends BuyAndDcaAbstractService {

    private final BuyActivityLogService buyActivityLogService;

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    @Autowired
    public BuyAlternativeService(AlternativeService alternativeService, CustomerService customerService, ProductsExpService productsExpService, InvestmentRequestClient investmentRequestClient, BuyActivityLogService buyActivityLogService) {
        super(alternativeService, customerService, productsExpService, investmentRequestClient);
        this.buyActivityLogService = buyActivityLogService;
    }

    /**
     * Generic Method to validate buy flow, and save activity log, then return response data
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param alternativeBuyRequest the alternative buy request
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    public TmbOneServiceResponse<String> validationBuy(String correlationId, String crmId, AlternativeBuyRequest alternativeBuyRequest) {
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            String processFlag = alternativeBuyRequest.getProcessFlag();
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId, crmId);

            tmbOneServiceResponse = validateProcessFlag(processFlag, tmbOneServiceResponse, status);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                String reason = tmbOneServiceResponse.getStatus().getDescription();
                return returnResponseAfterSavingActivityLog(correlationId, crmId, reason, alternativeBuyRequest, tmbOneServiceResponse);
            }

            tmbOneServiceResponse = validateBuyAndDca(correlationId, crmId, customerInfo, tmbOneServiceResponse, status, true, isFirstTrade(correlationId, alternativeBuyRequest));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                String reason = tmbOneServiceResponse.getStatus().getDescription();
                return returnResponseAfterSavingActivityLog(correlationId, crmId, reason, alternativeBuyRequest, tmbOneServiceResponse);
            }

            // validate suitability expired
            tmbOneServiceResponse = validateSuitabilityExpired(correlationId, crmId, tmbOneServiceResponse, status);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                String reason = tmbOneServiceResponse.getStatus().getDescription();
                return returnResponseAfterSavingActivityLog(correlationId, crmId, reason, alternativeBuyRequest, tmbOneServiceResponse);
            }

            return returnResponseAfterSavingActivityLog(correlationId, crmId, "", alternativeBuyRequest, tmbOneServiceResponse);

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    /**
     * Generic Method to check is process first trade
     *
     * @param correlationId         the correlation id
     * @param alternativeBuyRequest the alternative buy request
     * @return boolean
     */
    @LogAround
    private boolean isFirstTrade(String correlationId, AlternativeBuyRequest alternativeBuyRequest) throws TMBCommonException {
        try {
            Map<String, String> headerParameter = UtilMap.createHeader(correlationId);

            ResponseEntity<TmbOneServiceResponse<FirstTradeResponseBody>> tmbOneServiceResponse = investmentRequestClient
                    .getFirstTrade(headerParameter, FirstTradeRequestBody.builder()
                            .portfolioNumber(alternativeBuyRequest.getUnitHolderNumber())
                            .fundCode(alternativeBuyRequest.getFundCode())
                            .build());
            if (!tmbOneServiceResponse.getStatusCode().is2xxSuccessful()) {
                throw new TMBCommonException(
                        ResponseCode.FAILED.getCode(),
                        String.format(ProductsExpServiceConstant.SERVICE_IS_NOT_AVAILABLE, "get first trade failed"),
                        ResponseCode.FAILED.getService(),
                        HttpStatus.BAD_REQUEST, null);
            }
            return ProductsExpServiceConstant.INVESTMENT_FIRST_TRADE_FLAG
                    .equals(tmbOneServiceResponse.getBody().getData().getFirstTradeFlag());
        } catch (TMBCommonException ex) {
            logger.error(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, "get first trade failed");
            throw ex;
        }
    }

    private TmbOneServiceResponse<String> returnResponseAfterSavingActivityLog(String correlationId, String crmId, String reason,
                                                                               AlternativeBuyRequest alternativeBuyRequest,
                                                                               TmbOneServiceResponse<String> tmbOneServiceResponse) {
        buyActivityLogService.clickPurchaseButtonAtFundFactSheetScreen(correlationId, crmId, alternativeBuyRequest, reason);
        return tmbOneServiceResponse;
    }
}
