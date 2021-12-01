package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundFilterServiceTest {

    @InjectMocks
    public FundFilterService fundFilterService;

    @Mock
    private TMBLogger<FundFilterServiceTest> logger;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    private FundListBySuitScoreRequest fundListBySuitScoreRequest;
    void initFundFilterRq() {
        fundListBySuitScoreRequest = new FundListBySuitScoreRequest();
        fundListBySuitScoreRequest.setSuitScore("2");

    }

    /* ---------  Test filtered fund List based on suitScore ---------  */
    @Test
    void testFundFilter() throws Exception {

        initFundFilterRq();
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundListBySuitScoreRequest rq= new FundListBySuitScoreRequest();
        rq.setSuitScore(fundListBySuitScoreRequest.getSuitScore());

       TmbOneServiceResponse<FundListBySuitScoreBody> tmbFundListBySuitScore = new TmbOneServiceResponse<>();
        FundListBySuitScoreBody fundListBySuitScoreBody= mapper.readValue(Paths.get("src/test/resources/investment/fund_filter.json").toFile(), FundListBySuitScoreBody.class);;
        tmbFundListBySuitScore.setData(fundListBySuitScoreBody);
        when(investmentRequestClient.callInvestmentListFundInfoService(any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundListBySuitScore));

        //When
        FundListBySuitScoreBody actual = fundFilterService.getFundListBySuitScore(correlationId,"crmid",rq);
        Assertions.assertEquals(fundListBySuitScoreBody.getFundClassList(), actual.getFundClassList());
    }


    /* ---------  Test filtered fund List  Not Found based on suitScore ---------  */

    @Test
    void testFundFilterNotFound() throws Exception {

        initFundFilterRq();
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundListBySuitScoreRequest rq= new FundListBySuitScoreRequest();
        rq.setSuitScore(fundListBySuitScoreRequest.getSuitScore());

        TmbOneServiceResponse<FundListBySuitScoreBody> tmbFundListBySuitScore = new TmbOneServiceResponse<>();
        FundListBySuitScoreBody fundListBySuitScoreBody= mapper.readValue(Paths.get("src/test/resources/investment/fund_filter_not_fund.json").toFile(), FundListBySuitScoreBody.class);;
        tmbFundListBySuitScore.setData(fundListBySuitScoreBody);
        when(investmentRequestClient.callInvestmentListFundInfoService(any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundListBySuitScore));

        //When
        FundListBySuitScoreBody actual = fundFilterService.getFundListBySuitScore(correlationId,"crmid",rq);

        //Then
        Assertions.assertEquals(fundListBySuitScoreBody.getFundClassList(), actual.getFundClassList());
    }
}
