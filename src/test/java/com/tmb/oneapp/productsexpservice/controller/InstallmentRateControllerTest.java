package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentData;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
@RunWith(JUnit4.class)
public class InstallmentRateControllerTest {
    @Mock
    TMBLogger<InstallmentRateController> logger;
    @Mock
    CreditCardClient creditCardClient;
    @InjectMocks
    InstallmentRateController installmentRateController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        installmentRateController = new InstallmentRateController(creditCardClient);
    }

    @Test
    public void testGetLoanAccountDetail() {
        String correlationId="c83936c284cb398fA46CF16F399C";
        TmbOneServiceResponse<InstallmentRateResponse> serverResponse = new TmbOneServiceResponse<>();
        InstallmentRateResponse data= new InstallmentRateResponse();
        InstallmentData installment = new InstallmentData();
        installment.setCashChillChillModel("Test");
        data.setInstallmentData(installment);
        serverResponse.setData(data);

        ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> response = new ResponseEntity<>(serverResponse, HttpStatus.OK);
        when(creditCardClient.getInstallmentRate(anyString(), any())).thenReturn(response);

        InstallmentRateRequest requestBody = new InstallmentRateRequest();
        requestBody.setAmount("1234.00");
        requestBody.setGetAllDetailFlag("Y");
        requestBody.setGroupAccountId("0000000050080760015");
        requestBody.setDisbursementDate("2020-10-16");
        requestBody.setBillCycleCutDate("3");
        requestBody.setPromoSegment("CS7");
        requestBody.setCashChillChillFlag("Y");
        requestBody.setGetAllDetailFlag("N");
        ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> result = installmentRateController.getLoanAccountDetail(correlationId, requestBody);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }
}

