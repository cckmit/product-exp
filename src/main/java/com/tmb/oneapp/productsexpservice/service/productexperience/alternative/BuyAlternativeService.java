package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BuyAlternativeService extends BuyAndDcaAbstractService {

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    public BuyAlternativeService(AlternativeService alternativeService, CustomerService customerService, ProductsExpService productsExpService, InvestmentRequestClient investmentRequestClient) {
        super(alternativeService, customerService, productsExpService, investmentRequestClient);
    }

    @LogAround
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

            tmbOneServiceResponse = validateProcessFlag(processFlag,tmbOneServiceResponse,status);
            if(!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return tmbOneServiceResponse;
            }

            tmbOneServiceResponse = validateBuyAndDca(correlationId,crmId,customerInfo,tmbOneServiceResponse,status,true,isFirstTrade(correlationId,alternativeBuyRequest));
            if(!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return tmbOneServiceResponse;
            }

            // validate suitability expired
            tmbOneServiceResponse = validateSuitabilityExpired(correlationId,crmId,tmbOneServiceResponse,status);
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

    @LogAround
    private boolean isFirstTrade(String correlationId,AlternativeBuyRequest alternativeBuyRequest) throws TMBCommonException {
        try{
            Map<String, String> headerParameter = UtilMap.createHeader(correlationId);

            ResponseEntity<TmbOneServiceResponse<FirstTradeResponseBody>> tmbOneServiceResponse = investmentRequestClient
                    .getFirstTrade(headerParameter, FirstTradeRequestBody.builder()
                            .portfolioNumber(alternativeBuyRequest.getUnitHolderNumber())
                            .fundCode(alternativeBuyRequest.getFundCode())
                            .build());
            if (!tmbOneServiceResponse.getStatusCode().is2xxSuccessful()){
                throw new TMBCommonException(
                        ResponseCode.FAILED.getCode(),
                        String.format(ProductsExpServiceConstant.SERVICE_IS_NOT_AVAILABLE,"get first trade failed"),
                        ResponseCode.FAILED.getService(),
                        HttpStatus.BAD_REQUEST, null);
            }
            return ProductsExpServiceConstant.INVESTMENT_FIRST_TRADE_FLAG
                    .equals(tmbOneServiceResponse.getBody().getData().getFirstTradeFlag());
        }catch (TMBCommonException ex){
            logger.error(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE,"get first trade failed");
           throw ex;
        }
    }

}
