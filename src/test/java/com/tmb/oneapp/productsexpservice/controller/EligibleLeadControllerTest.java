package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadRequest;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Status;
import org.junit.Assert;
import org.junit.Before;
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
public class EligibleLeadControllerTest {
    @Mock
    TMBLogger<EligibleLeadController> logger;
    @Mock
    CreditCardClient creditCardClient;
    @InjectMocks
    EligibleLeadController eligibleLeadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eligibleLeadController = new EligibleLeadController(creditCardClient);
    }

    @Test
    public void testGetLoanAccountDetail() throws Exception {
        TmbOneServiceResponse<EligibleLeadResponse> serverResponse = new TmbOneServiceResponse<>();
        TmbStatus status= new TmbStatus();
        status.setService("card-money-transfer");
        status.setMessage("test");
        status.setDescription("test");
        status.setCode("0");
        serverResponse.setStatus(status);
        EligibleLeadResponse data= new EligibleLeadResponse();
        Status stat = new Status();
        stat.setAccountStatus("success");
        data.setStatus(stat);
        serverResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> response= new ResponseEntity<>(serverResponse, HttpStatus.OK);
        when(creditCardClient.getEligibleLeads(anyString(), any())).thenReturn(response);

        EligibleLeadRequest requestBody = new EligibleLeadRequest();
        requestBody.setGroupAccountId("1234");
        requestBody.setDisbursementDate("1234");
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> result = eligibleLeadController.getLoanAccountDetail("correlationId", requestBody);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }
}

