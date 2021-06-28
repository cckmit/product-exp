package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.fund.dailynav.response.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.response.InformationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentAsyncServiceTest {

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @InjectMocks
    private InvestmentAsyncService investmentAsyncService;


    @Test
    void should_return_information_body_when_call_fetch_fund_information_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        InformationResponse informationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_information.json").toFile(),
                InformationResponse.class);
        TmbOneServiceResponse<InformationBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(informationResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<InformationBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.getFundInformation(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<InformationBody> actual = investmentAsyncService.fetchFundInformation(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<InformationBody> expected = CompletableFuture.completedFuture(informationResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_fund_information_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getFundInformation(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchFundInformation(any(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_daily_nav_body_when_call_fetch_fund_daily_nav_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        DailyNavResponse dailyNavResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_daily_nav.json").toFile(),
                DailyNavResponse.class);
        TmbOneServiceResponse<DailyNavBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(dailyNavResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<DailyNavBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.getFundDailyNav(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<DailyNavBody> actual = investmentAsyncService.fetchFundDailyNav(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<DailyNavBody> expected = CompletableFuture.completedFuture(dailyNavResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_fund_daily_nav_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getFundDailyNav(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchFundDailyNav(any(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }
}