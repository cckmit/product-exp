package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.SetPinResponse;
import com.tmb.common.model.creditcard.SilverlakeErrorStatus;
import com.tmb.common.model.creditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.OneappAuthClient;
import com.tmb.oneapp.productsexpservice.model.setpin.Result;
import com.tmb.oneapp.productsexpservice.model.setpin.SetPinReqParameter;
import com.tmb.oneapp.productsexpservice.model.setpin.TranslatePinRes;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SetPinControllerTest {
    SetPinController setPinController;
    @Mock
    OneappAuthClient oneappAuthClient;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    CreditCardLogService creditCardLogService;

    @Mock
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        setPinController = new SetPinController(oneappAuthClient, creditCardClient, creditCardLogService, notificationService);
    }

    @Test
    void testGetSetPinSuccessStatusZero() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        SetPinReqParameter setPinReqParameter = new SetPinReqParameter();
        setPinReqParameter.setAccountId("0000000050078690018000095");
        setPinReqParameter.setAnb("0630000000095");
        setPinReqParameter.setE2eesid("QroqCnmkCJ3XtElAWaKAFklp1e3Hkd0OZUzh5n");
        setPinReqParameter.setRpin("244DD85E45182C18055F9954D8CC9416");
        TmbOneServiceResponse<SetPinResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TranslatePinRes translatePinRes = new TranslatePinRes();
        Result result = new Result();
        result.setBuffer("bC6bt7gKEMM=");
        translatePinRes.setResult(result);
        SetPinResponse setPinResponse = new SetPinResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        setPinResponse.setStatus(silverlakeStatus);
        oneServiceResponse.setData(setPinResponse);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService()));
        ResponseEntity<TmbOneServiceResponse<SetPinResponse>> openObj = new ResponseEntity<TmbOneServiceResponse<SetPinResponse>>(
                oneServiceResponse, HttpStatus.OK);
        when(creditCardClient.setPin(anyString(), any())).thenReturn(openObj);
        when(oneappAuthClient.fetchEcasTranslatePinData(anyString(), any())).thenReturn(translatePinRes);
        ResponseEntity<TmbOneServiceResponse<SetPinResponse>> res = setPinController.getSetPin(requestHeadersParameter,
                setPinReqParameter);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void testGetSetPinSuccessStatusOne() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        SetPinReqParameter setPinReqParameter = new SetPinReqParameter();
        setPinReqParameter.setAccountId("0000000050078690018000095");
        setPinReqParameter.setAnb("0630000000095");
        setPinReqParameter.setE2eesid("QroqCnmkCJ3XtElAWaKAFklp1e3Hkd0OZUzh5n");
        setPinReqParameter.setRpin("244DD85E45182C18055F9954D8CC9416");
        TmbOneServiceResponse<SetPinResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        SetPinResponse setPinResponse = new SetPinResponse();
        TranslatePinRes translatePinRes = new TranslatePinRes();

        Result result = new Result();
        result.setBuffer("bC6bt7gKEMM=");
        translatePinRes.setResult(result);
        SilverlakeStatus status = new SilverlakeStatus();
        setPinResponse.setStatus(status);
        status.setStatusCode(1);
        List<SilverlakeErrorStatus> errorStatus = new ArrayList<SilverlakeErrorStatus>();
        SilverlakeErrorStatus silverlakeErrorStatus = new SilverlakeErrorStatus();
        silverlakeErrorStatus.setDescription("123");
        silverlakeErrorStatus.setErrorCode("011");
        errorStatus.add(0, silverlakeErrorStatus);
        status.setErrorStatus(errorStatus);
        oneServiceResponse.setData(setPinResponse);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService()));
        ResponseEntity<TmbOneServiceResponse<SetPinResponse>> openObj = new ResponseEntity<TmbOneServiceResponse<SetPinResponse>>(
                oneServiceResponse, HttpStatus.OK);
        when(creditCardClient.setPin(anyString(), any())).thenReturn(openObj);
        when(oneappAuthClient.fetchEcasTranslatePinData(anyString(), any())).thenReturn(translatePinRes);
        ResponseEntity<TmbOneServiceResponse<SetPinResponse>> res = setPinController.getSetPin(requestHeadersParameter,
                setPinReqParameter);
        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void testGetSetPinException() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        SetPinReqParameter setPinReqParameter = new SetPinReqParameter();
        setPinReqParameter.setAccountId("0000000050078690018000095");
        setPinReqParameter.setAnb("0630000000095");
        setPinReqParameter.setE2eesid("QroqCnmkCJ3XtElAWaKAFklp1e3Hkd0OZUzh5n");
        setPinReqParameter.setRpin("244DD85E45182C18055F9954D8CC9416");
        when(creditCardClient.setPin(anyString(), any())).thenThrow(FeignException.BadRequest.class);
        Assertions.assertThrows(TMBCommonException.class, () -> {
            setPinController.getSetPin(requestHeadersParameter, setPinReqParameter);
        });

    }

    @Test
    void testGetSetPinFeignException() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        SetPinReqParameter setPinReqParameter = new SetPinReqParameter();
        setPinReqParameter.setAccountId("0000000050078690018000095");
        setPinReqParameter.setAnb("0630000000095");
        setPinReqParameter.setE2eesid("QroqCnmkCJ3XtElAWaKAFklp1e3Hkd0OZUzh5n");
        setPinReqParameter.setRpin("244DD85E45182C18055F9954D8CC9416");
        when(oneappAuthClient.fetchEcasTranslatePinData(anyString(), any())).thenThrow(FeignException.class);
        assertThrows(TMBCommonException.class, () -> {
            setPinController.getSetPin(requestHeadersParameter, setPinReqParameter);
        });

    }

    public Map<String, String> headerRequestParameter(String correlationId) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        return reqData;

    }


}
