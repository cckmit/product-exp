package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardBlockCodeResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCreditCardResponse;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreditCardControllerTest {
    CreditCardController creditCardController;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    CreditCardLogService creditCardLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        creditCardController = new CreditCardController(creditCardClient, creditCardLogService);

    }

    @Test
    void testVerifyCreditCardDetailsSuccess() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        GetCardBlockCodeResponse getCardBlockCodeResponse = new GetCardBlockCodeResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        getCardBlockCodeResponse.setStatus(silverlakeStatus);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setStatus(silverlakeStatus);
        handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.OK);
        handleGetCardResponse(fetchCardResponse, HttpStatus.OK);

        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
                .verifyCreditCardDetails(requestHeadersParameter);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    void testVerifyCreditCardDetailsNOData() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("", "");
        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
                .verifyCreditCardDetails(requestHeadersParameter);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    void testVerifyCreditCardDetailsError() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);

        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
                .verifyCreditCardDetails(requestHeadersParameter);
        assertNull(response.getBody().getData());

    }

    @Test
    void testVerifyCreditCardDetailsBlockCodeResNull() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        GetCardBlockCodeResponse getCardBlockCodeResponse = null;
        handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.BAD_REQUEST);

        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
                .verifyCreditCardDetails(requestHeadersParameter);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    void testVerifyCreditCardDetailsCardResNull() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        GetCardBlockCodeResponse getCardBlockCodeResponse = new GetCardBlockCodeResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        getCardBlockCodeResponse.setStatus(silverlakeStatus);
        handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.OK);
        FetchCardResponse fetchCardResponse = null;
        handleGetCardResponse(fetchCardResponse, HttpStatus.BAD_REQUEST);
        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
                .verifyCreditCardDetails(requestHeadersParameter);
        assertEquals(400, response.getStatusCodeValue());

    }

    public void handleGetCardBlockCodeResponse(GetCardBlockCodeResponse getCardBlockCodeResponse, HttpStatus status) {
        TmbOneServiceResponse<GetCardBlockCodeResponse> oneServiceResponse = new TmbOneServiceResponse<GetCardBlockCodeResponse>();
        oneServiceResponse.setData(getCardBlockCodeResponse);
        ResponseEntity<GetCardBlockCodeResponse> res = new ResponseEntity<GetCardBlockCodeResponse>(
                getCardBlockCodeResponse, status);
        when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenReturn(res);

    }

    public void handleGetCardResponse(FetchCardResponse fetchCardResponse, HttpStatus status) {
        TmbOneServiceResponse<FetchCardResponse> oneServiceResponse = new TmbOneServiceResponse<FetchCardResponse>();
        oneServiceResponse.setData(fetchCardResponse);
        ResponseEntity<FetchCardResponse> getCardRes = new ResponseEntity<FetchCardResponse>(fetchCardResponse, status);
        when(creditCardClient.getCreditCardDetails(anyString(), anyString())).thenReturn(getCardRes);

    }

    public Map<String, String> headerRequestParameter(String correlationId, String accountNo) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        reqData.put(ProductsExpServiceConstant.ACCOUNT_ID, accountNo);
        return reqData;

    }

    @Test
    void testDataNotFoundError() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("test", "test");
        TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        CreditCardEvent creditCardEvent = testData(oneServiceResponse);
        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> errorResponse = creditCardController.dataNotFoundError(httpHeaders, oneServiceResponse, creditCardEvent);
        assertEquals(400, errorResponse.getStatusCodeValue());
    }

    private CreditCardEvent testData(TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse) {
        VerifyCreditCardResponse data = new VerifyCreditCardResponse();
        data.setExpiryDate("test");
        data.setBlockCode("1234");
        data.setCreditCardRefId("1234");
        oneServiceResponse.setData(data);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setDescription("failure");
        tmbStatus.setCode("0001");
        tmbStatus.setMessage("Failure");
        tmbStatus.setDescription("Failed response");
        oneServiceResponse.setStatus(tmbStatus);
        CreditCardEvent creditCardEvent = new CreditCardEvent("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "test", "00700700");
        return creditCardEvent;
    }

    @Test
    void testGetFailedResponse() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("test", "test");
        TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        CreditCardEvent creditCardEvent = testData(oneServiceResponse);
        ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> failedResponse = creditCardController.getFailedResponse(responseHeaders, oneServiceResponse, creditCardEvent);
        assertEquals(400, failedResponse.getStatusCodeValue());
    }
}
