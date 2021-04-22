package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.ncb.NcbPaymentConfirmBody;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.oneapp.productsexpservice.service.NcbPaymentConfirmService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class NcbPaymentConfirmControllerTest {

    private final NcbPaymentConfirmService ncbPaymentConfirmService = Mockito.mock(NcbPaymentConfirmService.class);
    private final NcbPaymentConfirmController ncbPaymentConfirmController = new NcbPaymentConfirmController(ncbPaymentConfirmService);

    @Test
    void confirmNcbPayment_Success() throws TMBCommonException, JsonProcessingException {
        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = new NcbPaymentConfirmResponse();
        ncbPaymentConfirmResponse.setTransactionDate("2021-04-02T10:35");
        ncbPaymentConfirmResponse.setReferenceNo("1234567890");

        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ncbPaymentConfirmResponse);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        String requestBody = "{\n" +
                "            \"service_type_id\": \"NCBR\",\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"email\": \"sorawit.s@tcs.com\",\n" +
                "                \"address\": \"123/12sfwaefawefawefwaf\",\n" +
                "                \"delivery_method\": \"email\",\n" +
                "                \"account_number\": \"1234567890\"\n" +
                "        }";

        NcbPaymentConfirmBody ncbPaymentConfirmBody = new ObjectMapper().readValue(requestBody, NcbPaymentConfirmBody.class);

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, ncbPaymentConfirmBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void confirmNcbPayment_NoData() throws TMBCommonException, JsonProcessingException {
        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = new NcbPaymentConfirmResponse();
        ncbPaymentConfirmResponse.setTransactionDate("2021-04-02T10:35");
        ncbPaymentConfirmResponse.setReferenceNo("1234567890");

        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ncbPaymentConfirmResponse);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");

        String requestBody = "{\n" +
                "            \"service_type_id\": \"NCBR\",\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"email\": \"sorawit.s@tcs.com\",\n" +
                "                \"address\": \"123/12sfwaefawefawefwaf\",\n" +
                "                \"delivery_method\": \"email\",\n" +
                "                \"account_number\": \"1234567890\"\n" +
                "        }";

        NcbPaymentConfirmBody ncbPaymentConfirmBody = new ObjectMapper().readValue(requestBody, NcbPaymentConfirmBody.class);

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, ncbPaymentConfirmBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void confirmNcbPayment_Fail() throws TMBCommonException, JsonProcessingException {
        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");

        String requestBody = "{\n" +
                "            \"service_type_id\": \"NCBR\",\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"email\": \"sorawit.s@tcs.com\",\n" +
                "                \"address\": \"123/12sfwaefawefawefwaf\",\n" +
                "                \"delivery_method\": \"email\",\n" +
                "                \"account_number\": \"1234567890\"\n" +
                "        }";

        NcbPaymentConfirmBody ncbPaymentConfirmBody = new ObjectMapper().readValue(requestBody, NcbPaymentConfirmBody.class);

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, ncbPaymentConfirmBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}