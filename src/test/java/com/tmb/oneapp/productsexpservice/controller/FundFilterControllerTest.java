
package com.tmb.oneapp.productsexpservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreRequest;
import com.tmb.oneapp.productsexpservice.service.FundFilterService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class FundFilterControllerTest {


    @Mock
    private FundFilterService fundFilterService;

    @InjectMocks
    private FundFilterController fundFilterController;

    private FundListBySuitScoreRequest fundListBySuitScoreRequest;

    String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    void initFundFilterRq() {
        fundListBySuitScoreRequest = new FundListBySuitScoreRequest();
        fundListBySuitScoreRequest.setSuitScore("2");

    }


    /* ---------  Test filtered fund List based on suitScore ---------  */

    @Test
    void testFundFilter() throws Exception {
        initFundFilterRq();
        FundListBySuitScoreBody expectedResponse = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_filter.json").toFile(), FundListBySuitScoreBody.class);
            when(fundFilterService.getFundListBySuitScore(correlationId,"crmid", fundListBySuitScoreRequest)).thenReturn(expectedResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> actualResult = fundFilterController.getFundListBySuitScore(correlationId,"crmid",fundListBySuitScoreRequest);
        Assert.assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertEquals(expectedResponse.getFundClassList(), actualResult.getBody().getData().getFundClassList());

    }


    /* ---------  Test filtered fund List Not Found based on suitScore ---------  */

   @Test
    void testFundFilterNotFound() throws Exception {
        initFundFilterRq();
        FundListBySuitScoreBody expectedResponse = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_filter_not_fund.json").toFile(), FundListBySuitScoreBody.class);
            when(fundFilterService.getFundListBySuitScore(correlationId,"crmid", fundListBySuitScoreRequest)).thenReturn(expectedResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> actualResult = fundFilterController.getFundListBySuitScore(correlationId,"crmid", fundListBySuitScoreRequest);
       Assert.assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());

    }


}

