package com.tmb.oneapp.productsexpservice.service;


import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FundFilterService class will get fund Details from MF Service
 */
@Service
public class FundFilterService {


    private static final TMBLogger<FundFilterService> logger = new TMBLogger<>(FundFilterService.class);
    private final InvestmentRequestClient investmentRequestClient;

    /**
     * Instantiates a new Fund Filter Controller.
     *
     * @param investmentRequestClient the investment Request Client
     */
    @Autowired
    public FundFilterService(InvestmentRequestClient investmentRequestClient) {

        this.investmentRequestClient = investmentRequestClient;
    }

    /**
     * Get Fund List By SuitScore Body Response
     *
     * @param correlationId              the correlation id
     * @param fundListBySuitScoreRequest the fund list by suit score request
     * @return the  response
     */

    public FundListBySuitScoreBody getFundListBySuitScore(String correlationId, FundListBySuitScoreRequest fundListBySuitScoreRequest) {
        FundListBySuitScoreBody response = new FundListBySuitScoreBody();
        Map<String, String> investmentHeaderRequest = UtilMap.createHeader(correlationId);

        try {
            String suitScore = fundListBySuitScoreRequest.getSuitScore();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"listFundInfo", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(investmentHeaderRequest));
            ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> fundListBySuitScoreBodyResponse =
                    investmentRequestClient.callInvestmentListFundInfoService(investmentHeaderRequest);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"listFundInfo", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(fundListBySuitScoreBodyResponse.getBody()));

            List<FundClassListInfo> fundList = fundListBySuitScoreBodyResponse.getBody().getData().getFundClassList();
            return filterFundListBasedOnSuitScore(fundList, suitScore);
        } catch (Exception ex) {
            logger.info("error : {}", ex);
            response.setFundClassList(null);
            return response;
        }
    }

    /**
     * Get Filtered Fund List By SuitScore Body Response
     *
     * @param fundList  the fund List
     * @param suitScore the suitScore
     * @return the fund List By SuitScore Body Responses
     */
    private FundListBySuitScoreBody filterFundListBasedOnSuitScore(List<FundClassListInfo> fundList, String suitScore) {
        FundListBySuitScoreBody fundListBySuitScoreBodyResponses = new FundListBySuitScoreBody();
        switch (suitScore) {
            case "1":
                fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals("01"))
                        .collect(Collectors.toList()));
                break;
            case "2":
                fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals("01") || t.getRiskRate().equals("02") || t.getRiskRate().equals("03") || t.getRiskRate().equals("04"))
                        .collect(Collectors.toList()));
                break;
            case "3":
                fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals("01") || t.getRiskRate().equals("02") || t.getRiskRate().equals("03") || t.getRiskRate().equals("04")|| t.getRiskRate().equals("05"))
                        .collect(Collectors.toList()));
                break;
            case "4":
                fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals("01") || t.getRiskRate().equals("02") || t.getRiskRate().equals("03") || t.getRiskRate().equals("04")|| t.getRiskRate().equals("05") || t.getRiskRate().equals("06") || t.getRiskRate().equals("07"))
                        .collect(Collectors.toList()));
                break;
            case "5":
                fundListBySuitScoreBodyResponses.setFundClassList(fundList.stream().filter(t -> t.getRiskRate().equals("01") || t.getRiskRate().equals("02") || t.getRiskRate().equals("03") || t.getRiskRate().equals("04")|| t.getRiskRate().equals("05") || t.getRiskRate().equals("06") || t.getRiskRate().equals("07") || t.getRiskRate().equals("08"))
                        .collect(Collectors.toList()));
                break;
            default:
                fundListBySuitScoreBodyResponses.setFundClassList(Collections.emptyList());
        }

        return fundListBySuitScoreBodyResponses;

    }
}
