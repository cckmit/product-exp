package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request.FirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.request.TradeOccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class BuyFirstTradeService {

    private static final TMBLogger<BuyFirstTradeService> logger = new TMBLogger<>(BuyFirstTradeService.class);

    private final InvestmentAsyncService investmentAsyncService;

    @Autowired
    public BuyFirstTradeService(InvestmentAsyncService investmentAsyncService) {
        this.investmentAsyncService = investmentAsyncService;
    }

    public TmbOneServiceResponse<TradeOccupationResponse> tradeOuccupationInquiry(@Valid String correlationId, @Valid String crmId, @Valid TradeOccupationRequest tradeOccupationRequest){
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        try {
            CompletableFuture<OccupationInquiryResponseBody> occupationInquiry = investmentAsyncService.fetchOccupationInquiry(headerParameter,crmId);
            CompletableFuture<FirstTradeResponseBody> firstTrade = investmentAsyncService.getFirstTrade(headerParameter,FirstTradeRequestBody.builder()
                    .fundCode(tradeOccupationRequest.getFundCode())
                    .portfolioNumber(tradeOccupationRequest.getPortfolioNumber())
                    .build());
            CompletableFuture.allOf(firstTrade, occupationInquiry);
            FirstTradeResponseBody firstTradeResponseBody = firstTrade.get();
            OccupationInquiryResponseBody occupationInquiryResponseBody = occupationInquiry.get();
            tmbOneServiceResponse.setData(TradeOccupationResponse.builder()
                    .firstTradeFlag(firstTradeResponseBody.getFirstTradeFlag())
                    .requirePosition(occupationInquiryResponseBody.getRequirePosition())
                    .occupationCode(occupationInquiryResponseBody.getOccupationCode())
                    .occupationDescription(occupationInquiryResponseBody.getOccupationDescription())
                    .build());
            return tmbOneServiceResponse;

        }catch (Exception ex){
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }

    }

}
