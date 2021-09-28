package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.information.InformationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * InformationService class will get data from api services, and handle business criteria
 */
@Service
public class InformationService{

    private static final TMBLogger<InformationService> logger = new TMBLogger<>(InformationService.class);

    private InvestmentAsyncService investmentAsyncService;

    @Autowired
    public InformationService(InvestmentAsyncService investmentAsyncService) {
        this.investmentAsyncService = investmentAsyncService;
    }

    /**
     * Method getFundInformation to call MF Service getFundList
     *
     * @param correlationId
     * @param fundCodeRequestBody
     * @return InformationDto
     */
    @LogAround
    public InformationDto getFundInformation(String correlationId, FundCodeRequestBody fundCodeRequestBody) throws TMBCommonException {
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
        try {
            CompletableFuture<InformationBody> fetchFundInformation = investmentAsyncService.fetchFundInformation(investmentRequestHeader, fundCodeRequestBody);
            CompletableFuture<DailyNavBody> fetchFundDailyNav = investmentAsyncService.fetchFundDailyNav(investmentRequestHeader, fundCodeRequestBody);
            CompletableFuture.allOf(fetchFundInformation, fetchFundDailyNav);
            return InformationDto.builder()
                    .information(fetchFundInformation.get())
                    .dailyNav(fetchFundDailyNav.get())
                    .build();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return null;
    }
}
