package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CrmSubmitCaseServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);
    private final CaseService caseService = Mockito.mock(CaseService.class);

    private final CrmSubmitCaseService crmSubmitCaseService = new CrmSubmitCaseService(customerServiceClient, caseService);

    @Test
    void createNcbCase_Success_by_email() throws TMBCommonException {
        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");
        header.put("x-crmid", "001100000000000000000099999998");

        String firstnameTh = "NAME";
        String lastnameTh = "TEST";
        String firstnameEn = "NAME";
        String lastnameEn = "TEST";
        String note = "adwadaw";

        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

        Map<String, String> response = new HashMap<>();
        response.put(ProductsExpServiceConstant.CASE_NUMBER, "12312312");
        response.put(ProductsExpServiceConstant.TRANSACTION_DATE, requestDate);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitCustomerCase(any(), any(), any())).thenReturn(mockResponse);

        assertNotEquals(null, crmSubmitCaseService.createCrmCase(header, firstnameTh, lastnameTh, firstnameEn, lastnameEn, SERVICE_TYPE_MATRIC_CODE_PWA_SEND_EMAIL_TO_ADVISOR, note));
    }

    @Test
    void createNcbCase_Success_by_call() throws TMBCommonException {
        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");
        header.put("x-crmid", "001100000000000000000099999998");

        String firstnameTh = "NAME";
        String lastnameTh = "TEST";
        String firstnameEn = "NAME";
        String lastnameEn = "TEST";
        String note = "adwadaw";

        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

        Map<String, String> response = new HashMap<>();
        response.put(ProductsExpServiceConstant.CASE_NUMBER, "12312312");
        response.put(ProductsExpServiceConstant.TRANSACTION_DATE, requestDate);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitCustomerCase(any(), any(), any())).thenReturn(mockResponse);

        assertNotEquals(null, crmSubmitCaseService.createCrmCase(header, firstnameTh, lastnameTh, firstnameEn, lastnameEn, SERVICE_TYPE_MATRIC_CODE_PWA_CALL_TO_ADVISOR, note));
    }

    @Test
    void createNcbCase_Success_by_leave_msg() throws TMBCommonException {
        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");
        header.put("x-crmid", "001100000000000000000099999998");

        String firstnameTh = "NAME";
        String lastnameTh = "TEST";
        String firstnameEn = "NAME";
        String lastnameEn = "TEST";
        String note = "adwadaw";

        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

        Map<String, String> response = new HashMap<>();
        response.put(ProductsExpServiceConstant.CASE_NUMBER, "12312312");
        response.put(ProductsExpServiceConstant.TRANSACTION_DATE, requestDate);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitCustomerCase(any(), any(), any())).thenReturn(mockResponse);

        assertNotEquals(null, crmSubmitCaseService.createCrmCase(header, firstnameTh, lastnameTh, firstnameEn, lastnameEn, SERVICE_TYPE_MATRIC_CODE_PWA_SEND_MESSAGE_TO_ADVISOR, note));
    }

    @Test
    void createNcbCase_Fail() {
        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "");
        header.put("x-crmid", "");

        String firstnameTh = "a";
        String lastnameTh = "b";
        String firstnameEn = "c";
        String lastnameEn = "d";

        String serviceTypeMatrixCode = "O0001";

        String note = "adwadaw";

        when(customerServiceClient.submitCustomerCase(any(), any(), any())).thenThrow(new IllegalArgumentException());

        assertThrows(TMBCommonException.class, () -> crmSubmitCaseService.createCrmCase(header, firstnameTh, lastnameTh, firstnameEn, lastnameEn, serviceTypeMatrixCode, note));
    }
}