package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CrmSubmitCaseServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);

    private final CrmSubmitCaseService crmSubmitCaseService = new CrmSubmitCaseService(customerServiceClient);

    @Test
    void createNcbCase_Success() {
        String crmId = "001100000000000000000099999998";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String firstnameTh = "NAME";
        String lastnameTh = "TEST";
        String firstnameEn = "NAME";
        String lastnameEn = "TEST";
        String serviceTypeMatrixCode = "O0001";

        String caseRef = "12312312";
        Map<String, String> response = new HashMap<>();
        response.put("case_number", caseRef);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenReturn(mockResponse);

        assertNotEquals(null, crmSubmitCaseService.createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, serviceTypeMatrixCode));
    }

    @Test
    void createNcbCase_Fail() {
        String crmId = "123";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String firstnameTh = "a";
        String lastnameTh = "b";
        String firstnameEn = "c";
        String lastnameEn = "d";

        String serviceTypeMatrixCode = "O0001";

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenThrow(new IllegalArgumentException());

        assertEquals(new HashMap<>(), crmSubmitCaseService.createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, serviceTypeMatrixCode));
    }
}