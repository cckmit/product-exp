package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusCase;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CaseServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);
    private final CaseService caseService = new CaseService(customerServiceClient);

    @Test
    void getCaseStatus_firstTime_success() throws TMBCommonException {

        //getFirstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetFirstTimeUsageResponse.setData(null);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //postFirstTimeUsage
        TmbOneServiceResponse<String> mockPostFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockPostFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockPostFirstTimeUsageResponse.setData("1");

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockPostFirstTimeUsageResponse));

        //getCastStatus
        TmbOneServiceResponse<List<CaseStatusCase>> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetCaseStatusResponse.setData(Arrays.asList(
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("Closed"),
                new CaseStatusCase().setStatus("In Progress"),
                new CaseStatusCase().setStatus("Closed")
        ));

        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        CaseStatusResponse response =
                caseService.getCaseStatus("correlationId", "crmId", "deviceId", "CST");

        assertEquals(true, response.getFirstUsageExperience());
        assertEquals("CST", response.getServiceTypeId());
        assertEquals(2, response.getCompleted().size());
        assertEquals(3, response.getInProgress().size());

    }

    @Test
    void getCaseStatus_noData_exceptions_success() throws TMBCommonException {

        //getFirstTimeUsage
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new FeignException.FeignClientException(404, "Data not found", request, null));

        //postFirstTimeUsage
        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new IllegalArgumentException());

        //getCastStatus
        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenThrow(new FeignException.FeignClientException(404, "Data not found", request, null));

        CaseStatusResponse response =
                caseService.getCaseStatus("correlationId", "crmId", "deviceId", "CST");

        assertEquals(true, response.getFirstUsageExperience());
        assertEquals("CST", response.getServiceTypeId());
        assertEquals(0, response.getCompleted().size());
        assertEquals(0, response.getInProgress().size());

    }

    @Test
    void getFirstTimeUsage_generalException() {
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        assertThrows(TMBCommonException.class, () ->
                caseService.getFirstTimeUsage("crmId", "deviceId", "CST")
        );

    }

    @Test
    void getFirstTimeUsage_unexpectedError() {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenThrow(IllegalArgumentException.class);

        assertThrows(TMBCommonException.class, () ->
                caseService.getFirstTimeUsage("crmId", "deviceId", "CST")
        );

    }

    @Test
    void getFirstTimeUsage_null() throws TMBCommonException {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("CST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(null));

        CustomerFirstUsage response = caseService.getFirstTimeUsage("crmId", "deviceId", "CST");

        assertNull(response);

    }

    @Test
    void getCaseStatus_null() throws TMBCommonException {
        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(null));

        List<CaseStatusCase> response = caseService.getCaseStatus(anyString(), anyString());

        assertEquals(new ArrayList<>(), response);
    }

    @Test
    void getCaseStatus_generalException() {
        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException());

        assertThrows(TMBCommonException.class, () ->
                caseService.getCaseStatus(anyString(), anyString())
        );
    }

    @Test
    void getCaseStatus_unexpectedError() {
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getCaseStatus(anyString(), anyString()))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        assertThrows(TMBCommonException.class, () ->
                caseService.getCaseStatus(anyString(), anyString())
        );
    }

}