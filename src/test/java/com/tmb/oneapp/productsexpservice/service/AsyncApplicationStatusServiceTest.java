package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.HirePurchaseExperienceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.LoanData;
import com.tmb.oneapp.productsexpservice.model.LoanDetails;
import com.tmb.oneapp.productsexpservice.model.request.LoanStatusRequest;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class AsyncApplicationStatusServiceTest {

    private final HirePurchaseExperienceClient hirePurchaseExperienceClient =
            Mockito.mock(HirePurchaseExperienceClient.class);
    private final LendingServiceClient lendingServiceClient =
            Mockito.mock(LendingServiceClient.class);

    AsyncApplicationStatusService asyncApplicationStatusService =
            new AsyncApplicationStatusService(hirePurchaseExperienceClient, lendingServiceClient);

    @Test
    void getHpData() throws TMBCommonException, ExecutionException, InterruptedException {
        //GET /apis/hpservice/loan-status/application-list
        TmbOneServiceResponse<LoanData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(new LoanData()
                .setProviderUserID("1260200195979")
                .setWSRecord(Arrays.asList(
                        new LoanDetails()
                                .setHPAPStatus("PRE")
                                .setAppNo("24639219656"),
                        new LoanDetails()
                                .setHPAPStatus("CK")
                                .setAppNo("24639219773")
                )));

        when(hirePurchaseExperienceClient.
                postLoanStatusApplicationList(anyString(), any(LoanStatusRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));


        //GET /apis/hpservice/loan-status/application-detail
        TmbOneServiceResponse<LoanData> mockGetCaseStatusResponse2
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse2.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse2.setData(new LoanData()
                .setProviderUserID("1260200195979")
                .setWSRecord(Collections.singletonList(
                        new LoanDetails()
                                .setAppNo("24639219656")
                                .setCarBrand("Toyota")
                )));

        TmbOneServiceResponse<LoanData> mockGetCaseStatusResponse3
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse3.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse3.setData(new LoanData()
                .setProviderUserID("1260200195979")
                .setWSRecord(Collections.singletonList(
                        new LoanDetails()
                                .setAppNo("24639219773")
                                .setCarBrand("Honda")
                )));

        when(hirePurchaseExperienceClient.
                postLoanStatusApplicationDetail(anyString(),
                        any(LoanStatusRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse2))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse3));

        CompletableFuture<List<LoanDetails>> response = asyncApplicationStatusService.getHpData(
                "correlationId", "en", "1260200195979", "000000000");

        assertEquals(2, response.get().size());

    }

    @Test
    void getHpData_list_feignException() throws TMBCommonException {
        //GET /apis/hpservice/loan-status/application-list
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(hirePurchaseExperienceClient.
                postLoanStatusApplicationList(anyString(), any(LoanStatusRequest.class)))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        CompletableFuture<List<LoanDetails>> response = asyncApplicationStatusService.getHpData(
                "correlationId", "en", "1260200195979", "000000000");

        assertNull(response);
    }

    @Test
    void getHpData_detail_exception() throws TMBCommonException {
        //GET /apis/hpservice/loan-status/application-list
        TmbOneServiceResponse<LoanData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(new LoanData()
                .setProviderUserID("1260200195979")
                .setWSRecord(Arrays.asList(
                        new LoanDetails()
                                .setHPAPStatus("PRE")
                                .setAppNo("24639219656"),
                        new LoanDetails()
                                .setHPAPStatus("CK")
                                .setAppNo("24639219773")
                )));

        when(hirePurchaseExperienceClient.
                postLoanStatusApplicationList(anyString(), any(LoanStatusRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //GET /apis/hpservice/loan-status/application-detail
        when(hirePurchaseExperienceClient.
                postLoanStatusApplicationDetail(anyString(),
                        any(LoanStatusRequest.class)))
                .thenThrow(IllegalArgumentException.class);

        CompletableFuture<List<LoanDetails>> response = asyncApplicationStatusService.getHpData(
                "correlationId", "en", "1260200195979", "000000000");

        assertNull(response);
    }

    @Test
    void getRSLData() throws TMBCommonException, ExecutionException, InterruptedException {
        //GET /apis/hpservice/loan-status/application-list
        TmbOneServiceResponse<List<LendingRslStatusResponse>> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(Arrays.asList(
                new LendingRslStatusResponse()
                        .setStatus("completed")
                        .setCurrentNode("1"),
                new LendingRslStatusResponse()
                        .setStatus("approved")
                        .setCurrentNode("2")
        ));

        when(lendingServiceClient.getLendingRslStatus(anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        CompletableFuture<List<LendingRslStatusResponse>> response = asyncApplicationStatusService
                .getRSLData("correlationId", "nationalId", "mobileNo");

        assertEquals("completed", response.get().get(0).getStatus());
        assertEquals("approved", response.get().get(1).getStatus());

    }

    @Test
    void getRSLData_feignException() throws TMBCommonException {
        //GET /apis/hpservice/loan-status/application-list
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(lendingServiceClient.getLendingRslStatus(anyString(), anyString(), anyString()))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        CompletableFuture<List<LendingRslStatusResponse>> response = asyncApplicationStatusService
                .getRSLData("correlationId", "nationalId", "mobileNo");

        assertNull(response);
    }
}