package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buyfirstrade.request.AlternativeBuyFirstTTradeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request.FirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The buy first trade alternative service.
 */
@Service
public class BuyFirstTradeAlternativeService {


    private static final TMBLogger<BuyFirstTradeAlternativeService> logger = new TMBLogger<>(BuyFirstTradeAlternativeService.class);

    private final CustomerService customerService;

    private final AlternativeService alternativeService;

    private final InvestmentAsyncService investmentAsyncService;

    @Autowired
    public BuyFirstTradeAlternativeService(CustomerService customerService, AlternativeService alternativeService, InvestmentAsyncService investmentAsyncService) {
        this.customerService = customerService;
        this.alternativeService = alternativeService;
        this.investmentAsyncService = investmentAsyncService;
    }


    /**
     * Generic Method to validate buy first trade flow
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param alternativeBuyFirstTTradeRequest the alternative buy request
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    public TmbOneServiceResponse<TradeOccupationResponse> validationBuyFirstTrade(String correlationId, String crmId, AlternativeBuyFirstTTradeRequest alternativeBuyFirstTTradeRequest) throws TMBCommonException {
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId, crmId);

            TradeOccupationResponse tradeAndOccupationInquiry = getFirstTradeAndOccupationInquiry(correlationId,crmId,alternativeBuyFirstTTradeRequest.getPortfolioNumber(),alternativeBuyFirstTTradeRequest.getFundCode());

            if(StringUtils.isEmpty(tradeAndOccupationInquiry)){
                tmbOneServiceResponse.setStatus(null);
                tmbOneServiceResponse.setData(null);
                return tmbOneServiceResponse;
            }

            tmbOneServiceResponse.setData(tradeAndOccupationInquiry);
            String isFirstTradeString = StringUtils.isEmpty(tradeAndOccupationInquiry.getFirstTradeFlag())?"Y":tradeAndOccupationInquiry.getFirstTradeFlag();
            boolean isFirstTrade = isFirstTradeString.equals("Y");
            if(isFirstTrade){
                return tmbOneServiceResponse;
            }

            // validate customer risk level
            BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(true).isFirstTrade(isFirstTrade).build();
            tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId,customerInfo, status,buyFlowFirstTrade));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                if(!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SERVICE_NOT_READY)){
                    tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
                }
                return tmbOneServiceResponse;
            }

            // validate id card expired
            tmbOneServiceResponse.setStatus(alternativeService.validateIdCardExpired(crmId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate flatca flag not valid
            tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid(customerInfo.getFatcaFlag(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate customer assurange level
            tmbOneServiceResponse.setStatus(alternativeService.validateIdentityAssuranceLevel(customerInfo.getEkycIdentifyAssuranceLevel(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                // use same error code with customer in risk C3 And B3
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
                return tmbOneServiceResponse;
            }

            // validate customer not us and not restriced in 30 nationality
            tmbOneServiceResponse.setStatus(alternativeService.validateNationality(correlationId, customerInfo.getNationality(), customerInfo.getNationalitySecond(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
                return tmbOneServiceResponse;
            }

            return tmbOneServiceResponse;

        } catch (TMBCommonException e) {
            throw e;
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
     * @param portfolioNumber      the unitHolderNumber
     * @param fundCode              the fundCode
     * @return boolean
     */
    @LogAround
    private TradeOccupationResponse getFirstTradeAndOccupationInquiry(String correlationId,String crmId, String portfolioNumber, String fundCode) throws TMBCommonException{
        TradeOccupationResponse tradeOccupationResponse = new TradeOccupationResponse();
        try {
            Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
            CompletableFuture<OccupationInquiryResponseBody> occupationInquiry = investmentAsyncService.fetchOccupationInquiry(headerParameter, crmId);
            CompletableFuture<FirstTradeResponseBody> firstTrade = investmentAsyncService.getFirstTrade(headerParameter, FirstTradeRequestBody.builder()
                    .fundCode(fundCode)
                    .portfolioNumber(portfolioNumber)
                    .build());
            CompletableFuture.allOf(firstTrade, occupationInquiry);
            FirstTradeResponseBody firstTradeResponseBody = firstTrade.get();
            OccupationInquiryResponseBody occupationInquiryResponseBody = occupationInquiry.get();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fisrtTrade", ProductsExpServiceConstant.LOGGING_RESPONSE),  UtilMap.convertObjectToStringJson(firstTradeResponseBody));
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchOccupationInquiry", ProductsExpServiceConstant.LOGGING_RESPONSE),  UtilMap.convertObjectToStringJson(occupationInquiryResponseBody));

            tradeOccupationResponse.setFirstTradeFlag(firstTradeResponseBody.getFirstTradeFlag());
            tradeOccupationResponse.setRequirePosition(occupationInquiryResponseBody.getRequirePosition());
            tradeOccupationResponse.setRequireUpdate(occupationInquiryResponseBody.getRequireUpdate());
            tradeOccupationResponse.setOccupationCode(occupationInquiryResponseBody.getOccupationCode());
            tradeOccupationResponse.setOccupationDescription(occupationInquiryResponseBody.getOccupationDescription());
            return tradeOccupationResponse;
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, "get first trade failed");
        }

        return tradeOccupationResponse;
    }

}
