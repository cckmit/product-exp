package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeErrorStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCvvResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProductsVerifyCvvControllerTest {

    ProductsVerifyCvvController productsVerifyCvvController;
    @Mock
    CreditCardClient creditCardClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        productsVerifyCvvController = new ProductsVerifyCvvController(creditCardClient);

    }

    @Test
    void testActivateCardDetailsSuccess() throws Exception {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167", "896", "2506");
        VerifyCvvResponse verifyCvvResponse = new VerifyCvvResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        SilverlakeErrorStatus silverlakeErrorStatus = new SilverlakeErrorStatus();
        silverlakeErrorStatus.setDescription("test");
        silverlakeErrorStatus.setErrorCode("0");
        List<SilverlakeErrorStatus> errorStatus = new ArrayList<SilverlakeErrorStatus>();
        silverlakeStatus.setStatusCode(0);
        silverlakeStatus.setErrorStatus(errorStatus);
        verifyCvvResponse.setStatus(silverlakeStatus);
        handleGetCardResponse(verifyCvvResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> responseEntity = productsVerifyCvvController
                .verifyCvv(reqHeaders);
        assertEquals(400, responseEntity.getStatusCodeValue());

    }


    @Test
    void testActivateCardDetailsElseCondition() throws Exception {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167", "896", "2506");
        VerifyCvvResponse verifyCvvResponse = new VerifyCvvResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        SilverlakeErrorStatus silverlakeErrorStatus = new SilverlakeErrorStatus();
        silverlakeErrorStatus.setDescription("test");
        silverlakeErrorStatus.setErrorCode("0");
        List<SilverlakeErrorStatus> errorStatus = new ArrayList<SilverlakeErrorStatus>();
        silverlakeStatus.setStatusCode(0);
        silverlakeStatus.setErrorStatus(errorStatus);
        verifyCvvResponse.setStatus(silverlakeStatus);
        handleGetCardResponse(verifyCvvResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> value = null;
        when(creditCardClient.verifyCvv(any())).thenReturn(value);

        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> activateCardResponse = productsVerifyCvvController
                .verifyCvv(reqHeaders);
        assertEquals(400, activateCardResponse.getStatusCodeValue());

    }

    @Test
    void testVerifyCvvDetailsDataNotFound() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167", "896", "2506");
        VerifyCvvResponse verifyCvvResponse = new VerifyCvvResponse();
        verifyCvvResponse.setStatus(new SilverlakeStatus());
        handleGetCardResponse(verifyCvvResponse, HttpStatus.BAD_REQUEST);
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> res = productsVerifyCvvController
                .verifyCvv(requestHeadersParameter);
        assertEquals(400, res.getStatusCodeValue());

    }

    @Test
    void testVerifyCvvDetailsError() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167", "", "");
        when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);

        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> response = productsVerifyCvvController
                .verifyCvv(requestHeadersParameter);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    void testVerifyCvvDetailsDataNotAvailable() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(ProductsExpServiceConstant.ACCOUNT_ID, "");
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> response = productsVerifyCvvController
                .verifyCvv(headers);
        assertEquals(400, response.getStatusCodeValue());


    }

    @Test
    void testVerifyCvvDetailsData() {
        Map<String, String> headers = new HashMap<>();
        headers.put(ProductsExpServiceConstant.ACCOUNT_ID, "");
        TmbOneServiceResponse<VerifyCvvResponse> resp = new TmbOneServiceResponse<>();
        VerifyCvvResponse data = new VerifyCvvResponse();
        SilverlakeStatus status = new SilverlakeStatus();
        status.setStatusCode(0);
        data.setStatus(status);
        resp.setData(data);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");
        tmbStatus.setService("verify-cvv-service");
        tmbStatus.setDescription("verify-cvv");
        tmbStatus.setDescription("Success");
        resp.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> value = new ResponseEntity<>(resp, HttpStatus.OK);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> response = productsVerifyCvvController
                .getTmbOneServiceResponseResponseEntity(data, resp, responseHeaders, value);
        assertEquals(200, response.getStatusCodeValue());


    }

    public Map<String, String> headerRequestParameter(String correlationId, String accountNo, String cvv,
                                                      String cardExpiry) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "c83936c284cb398fA46CF16F399C");
        headers.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078360018000167");
        headers.put(ProductsExpServiceConstant.CVV, "896");
        headers.put(ProductsExpServiceConstant.CARD_EXPIRY, "2506");
        return headers;

    }

    public void handleGetCardResponse(VerifyCvvResponse verifyCvvResponse, HttpStatus status) {
        TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse = new TmbOneServiceResponse<VerifyCvvResponse>();
        oneServiceResponse.setData(verifyCvvResponse);
        ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> getCardRes = new ResponseEntity(verifyCvvResponse, status);
        when(creditCardClient.verifyCvv(any())).thenReturn(getCardRes);

    }

    @Test
    void failedResponse() {
        VerifyCvvResponse response = new VerifyCvvResponse();
        SilverlakeStatus status = new SilverlakeStatus();
        status.setStatusCode(0);
        response.setStatus(status);
        TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setDescription("Failed response");
        oneServiceResponse.setStatus(tmbStatus);
        String code="0001";
        String message="failed Response";
        String service="verify-cvv service";
        productsVerifyCvvController.failedResponse(response,oneServiceResponse,code,message,service);
        assertNotNull(oneServiceResponse);
    }
}
