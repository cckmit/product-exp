package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.LoanDetails;
import com.tmb.oneapp.productsexpservice.model.response.NodeDetails;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class ApplicationStatusServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);
    private final AsyncApplicationStatusService asyncApplicationStatusService = Mockito.mock(AsyncApplicationStatusService.class);
    private final CommonServiceClient commonServiceClient = Mockito.mock(CommonServiceClient.class);

    private final ApplicationStatusService applicationStatusService = new ApplicationStatusService(
            customerServiceClient, asyncApplicationStatusService, commonServiceClient);

    @Test
    void getApplicationStatus_en() throws TMBCommonException {

        //GET /apis/customers/{crmId}
        TmbOneServiceResponse<CustomerProfileResponseData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        CustomerProfileResponseData customerProfileResponseData = new CustomerProfileResponseData();
        customerProfileResponseData.setIdNo("nationalId");
        customerProfileResponseData.setPhoneNoFull("mobileNo");
        mockGetCaseStatusResponse.setData(customerProfileResponseData);

        when(customerServiceClient.getCustomerProfile(anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //roadMap
        TmbOneServiceResponse<List<NodeDetails>> mockGetProductApplicationRoadMap
                = new TmbOneServiceResponse<>();
        mockGetProductApplicationRoadMap.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetProductApplicationRoadMap.setData(Arrays.asList(
                new NodeDetails()
                        .setLoanSystem("HP")
                        .setNodeEn(Arrays.asList("HP", "HP"))
                        .setNodeTh(Arrays.asList("HPth", "HPth")),
                new NodeDetails()
                        .setLoanSystem("RSL")
                        .setNodeEn(Arrays.asList("RSL", "RSL"))
                        .setNodeTh(Arrays.asList("RSL", "RSL"))
        ));

        when(commonServiceClient.getProductApplicationRoadMap())
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetProductApplicationRoadMap));

        //HP
        when(asyncApplicationStatusService.getHpData(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        Arrays.asList(new LoanDetails()
                                        .setHPAPStatus("PRE")
                                        .setStatusDate("16/10/2020 17:08:49")
                                        .setCarBrand("Toyota")
                                        .setCarFamily("Camry"),
                                new LoanDetails()
                                        .setHPAPStatus("CC3+N")
                                        .setStatusDate("19/10/2020 17:08:49")
                                        .setCarBrand("Toyota")
                                        .setCarFamily("Camry")
                        ))
                );

        //RSL
        when(asyncApplicationStatusService.getRSLData(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        Arrays.asList(new LendingRslStatusResponse()
                                        .setAppType("CC")
                                        .setStatus("in_progress")
                                        .setProductCode("MO")
                                        .setLastUpdateDate("2020-07-17T09:39:25")
                                        .setCurrentNode("2")
                                        .setIsApproved("N")
                                        .setIsRejected("Y"),
                                new LendingRslStatusResponse()
                                        .setAppType("SM")
                                        .setStatus("in_progress")
                                        .setProductCode("PL")
                                        .setCurrentNode("1")
                                        .setApplicationDate("2020-07-16T09:39:25")
                        ))
                );


        //GET /apis/customers/firstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetFirstTimeUsageResponse.setData(null);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //POST /apis/customers/firstTimeUsage
        TmbOneServiceResponse<String> mockPostFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockPostFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockPostFirstTimeUsageResponse.setData("1");

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockPostFirstTimeUsageResponse));

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ApplicationStatusResponse response = applicationStatusService.getApplicationStatus(header, "AST");

        assertEquals(true, response.getFirstUsageExperience());
        assertEquals("HP", response.getCompleted().get(0).getProductCode());
        assertEquals("Toyota Camry", response.getCompleted().get(0).getProductDetailEn());

    }

    @Test
    void getApplicationStatus_hpException_rslNoData() throws TMBCommonException {

        //GET /apis/customers/{crmId}
        TmbOneServiceResponse<CustomerProfileResponseData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        CustomerProfileResponseData customerProfileResponseData = new CustomerProfileResponseData();
        customerProfileResponseData.setIdNo("nationalId");
        customerProfileResponseData.setPhoneNoFull("mobileNo");
        mockGetCaseStatusResponse.setData(customerProfileResponseData);

        when(customerServiceClient.getCustomerProfile( anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //roadMap
        TmbOneServiceResponse<List<NodeDetails>> mockGetProductApplicationRoadMap
                = new TmbOneServiceResponse<>();
        mockGetProductApplicationRoadMap.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetProductApplicationRoadMap.setData(Collections.singletonList(
                new NodeDetails()
                        .setLoanSystem("RSL")
                        .setNodeEn(Arrays.asList("RSL", "RSL"))
                        .setNodeTh(Arrays.asList("RSL", "RSL"))
        ));

        when(commonServiceClient.getProductApplicationRoadMap())
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetProductApplicationRoadMap));

        //HP
        when(asyncApplicationStatusService.getHpData(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        Arrays.asList(new LoanDetails()
                                        .setHPAPStatus("PRE")
                                        .setStatusDate("16/10/2020 17:08:49")
                                        .setCarBrand("Toyota")
                                        .setCarFamily("Camry"),
                                new LoanDetails()
                                        .setHPAPStatus("CC3+N")
                                        .setStatusDate("19/10/2020 17:08:49")
                                        .setCarBrand("Toyota")
                                        .setCarFamily("Camry")
                        ))
                );

        //RSL
        when(asyncApplicationStatusService.getRSLData(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        Arrays.asList(new LendingRslStatusResponse()
                                        .setStatus("in_progress")
                                        .setProductCode("MO")
                                        .setLastUpdateDate("2020-07-17T09:39:25")
                                        .setCurrentNode("2")
                                        .setIsApproved("N")
                                        .setIsRejected("Y"),
                                new LendingRslStatusResponse()
                                        .setStatus("in_progress")
                                        .setProductCode("PL")
                                        .setCurrentNode("1")
                                        .setApplicationDate("2020-07-16T09:39:25")
                        ))
                );


        //GET /apis/customers/firstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetFirstTimeUsageResponse.setData(null);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //POST /apis/customers/firstTimeUsage
        TmbOneServiceResponse<String> mockPostFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockPostFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockPostFirstTimeUsageResponse.setData("1");

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockPostFirstTimeUsageResponse));

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        assertThrows(TMBCommonException.class, () ->
                applicationStatusService.getApplicationStatus(header, "AST")
        );

    }

    @Test
    void getApplicationStatus_dataNotFound() throws TMBCommonException {

        //GET /apis/customers/{crmId}
        TmbOneServiceResponse<CustomerProfileResponseData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        CustomerProfileResponseData customerProfileResponseData = new CustomerProfileResponseData();
        customerProfileResponseData.setIdNo("nationalId");
        customerProfileResponseData.setPhoneNoFull("mobileNo");
        mockGetCaseStatusResponse.setData(customerProfileResponseData);

        when(customerServiceClient.getCustomerProfile(anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //roadMap
        TmbOneServiceResponse<List<NodeDetails>> mockGetProductApplicationRoadMap
                = new TmbOneServiceResponse<>();
        mockGetProductApplicationRoadMap.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetProductApplicationRoadMap.setData(Arrays.asList(
                new NodeDetails()
                        .setLoanSystem("HP")
                        .setNodeEn(Arrays.asList("HP", "HP"))
                        .setNodeTh(Arrays.asList("HPth", "HPth")),
                new NodeDetails()
                        .setLoanSystem("RSL")
                        .setNodeEn(Arrays.asList("RSL", "RSL"))
                        .setNodeTh(Arrays.asList("RSL", "RSL"))
        ));

        when(commonServiceClient.getProductApplicationRoadMap())
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetProductApplicationRoadMap));

        //HP
        when(asyncApplicationStatusService.getHpData(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        //RSL
        when(asyncApplicationStatusService.getRSLData(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));


        //GET /apis/customers/firstTimeUsage
        TmbOneServiceResponse<CustomerFirstUsage> mockGetFirstTimeUsageResponse
                = new TmbOneServiceResponse<>();
        mockGetFirstTimeUsageResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetFirstTimeUsageResponse.setData(null);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetFirstTimeUsageResponse));

        //POST /apis/customers/firstTimeUsage
        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ApplicationStatusResponse response = applicationStatusService.getApplicationStatus(header, "AST");

        assertTrue(response.getInProgress().isEmpty());
        assertTrue(response.getInProgress().isEmpty());

    }

    @Test
    void getApplicationStatus_tmbException() throws TMBCommonException {

        //GET /apis/customers/{crmId}
        TmbOneServiceResponse<CustomerProfileResponseData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        CustomerProfileResponseData customerProfileResponseData = new CustomerProfileResponseData();
        customerProfileResponseData.setIdNo("nationalId");
        customerProfileResponseData.setPhoneNoFull("mobileNo");
        mockGetCaseStatusResponse.setData(customerProfileResponseData);

        when(customerServiceClient.getCustomerProfile(anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //roadMap
        TmbOneServiceResponse<List<NodeDetails>> mockGetProductApplicationRoadMap
                = new TmbOneServiceResponse<>();
        mockGetProductApplicationRoadMap.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetProductApplicationRoadMap.setData(Arrays.asList(
                new NodeDetails()
                        .setLoanSystem("HP")
                        .setNodeEn(Arrays.asList("HP", "HP"))
                        .setNodeTh(Arrays.asList("HPth", "HPth")),
                new NodeDetails()
                        .setLoanSystem("RSL")
                        .setNodeEn(Arrays.asList("RSL", "RSL"))
                        .setNodeTh(Arrays.asList("RSL", "RSL"))
        ));

        when(commonServiceClient.getProductApplicationRoadMap())
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetProductApplicationRoadMap));

        //HP
        when(asyncApplicationStatusService.getHpData(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(TMBCommonException.class);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        assertThrows(TMBCommonException.class, () ->
                applicationStatusService.getApplicationStatus(header, "AST"));

    }

    @Test
    void getApplicationStatus_generalException() throws TMBCommonException {

        //GET /apis/customers/{crmId}
        TmbOneServiceResponse<CustomerProfileResponseData> mockGetCaseStatusResponse
                = new TmbOneServiceResponse<>();
        mockGetCaseStatusResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        CustomerProfileResponseData customerProfileResponseData = new CustomerProfileResponseData();
        customerProfileResponseData.setIdNo("nationalId");
        customerProfileResponseData.setPhoneNoFull("mobileNo");
        mockGetCaseStatusResponse.setData(customerProfileResponseData);

        when(customerServiceClient.getCustomerProfile(anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetCaseStatusResponse));

        //roadMap
        TmbOneServiceResponse<List<NodeDetails>> mockGetProductApplicationRoadMap
                = new TmbOneServiceResponse<>();
        mockGetProductApplicationRoadMap.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        mockGetProductApplicationRoadMap.setData(Arrays.asList(
                new NodeDetails()
                        .setLoanSystem("HP")
                        .setNodeEn(Arrays.asList("HP", "HP"))
                        .setNodeTh(Arrays.asList("HPth", "HPth")),
                new NodeDetails()
                        .setLoanSystem("RSL")
                        .setNodeEn(Arrays.asList("RSL", "RSL"))
                        .setNodeTh(Arrays.asList("RSL", "RSL"))
        ));

        when(commonServiceClient.getProductApplicationRoadMap())
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(mockGetProductApplicationRoadMap));

        //HP
        when(asyncApplicationStatusService.getHpData(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(IllegalArgumentException.class);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        assertThrows(TMBCommonException.class, () ->
                applicationStatusService.getApplicationStatus(header, "AST"));


    }

    @Test
    void getFirstTimeUsage_generalException() {
        Request request = Request.create(Request.HttpMethod.GET,
                "",
                new HashMap<>(),
                null,
                new RequestTemplate());

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenThrow(new FeignException.FeignClientException(401, "Unauthorized", request, null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () ->
                applicationStatusService.getFirstTimeUsage(header, "AST")
        );

    }

    @Test
    void getFirstTimeUsage_unexpectedError() {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenThrow(IllegalArgumentException.class);

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        assertThrows(TMBCommonException.class, () ->
                applicationStatusService.getFirstTimeUsage(header, "AST")
        );

    }

    @Test
    void getFirstTimeUsage_null() throws TMBCommonException {
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("AST")))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(null));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        CustomerFirstUsage response = applicationStatusService.getFirstTimeUsage(header, "AST");

        assertNull(response);

    }

    @Test
    void getStatusTest() {
        assertEquals(2, applicationStatusService.getStatus(new ArrayList<>()));

    }

    @Test
    void testDataNotFound() {
        CustomerFirstUsage result = applicationStatusService.dataNotFound("crmId", "deviceId");
        CustomerFirstUsage expected = new CustomerFirstUsage();
        expected.setServiceTypeId("123");
        expected.setCrmId("123");
        expected.setServiceTypeId("test");
        expected.setTimestamp("test");
        Assertions.assertNotEquals(expected, result);
    }

}