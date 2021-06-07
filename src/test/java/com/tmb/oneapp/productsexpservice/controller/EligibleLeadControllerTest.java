package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadRequest;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentPromotion;
import com.tmb.oneapp.productsexpservice.model.loan.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        TmbStatus status = getTmbStatus();
        serverResponse.setStatus(status);
        EligibleLeadResponse data = new EligibleLeadResponse();
        Status stat = new Status();
        stat.setAccountStatus("success");
        data.setStatus(stat);
        serverResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> response = new ResponseEntity<>(serverResponse, HttpStatus.OK);
        when(creditCardClient.getEligibleLeads(anyString(), any())).thenReturn(response);

        EligibleLeadRequest requestBody = new EligibleLeadRequest();
        requestBody.setGroupAccountId("1234");
        requestBody.setDisbursementDate("1234");
        Map<String, String> headers = new HashMap<String, String>();
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> result = eligibleLeadController.getLoanEligibleDetail(headers, requestBody);
        assertNotEquals(200, result.getStatusCodeValue());
    }

    private TmbStatus getTmbStatus() {
        TmbStatus status = new TmbStatus();
        status.setService("card-money-transfer");
        status.setMessage("test");
        status.setDescription("test");
        status.setCode("0");
        return status;
    }

    @Test
    public void testTestGetLoanAccountDetail() throws Exception {


        final TmbOneServiceResponse<EligibleLeadResponse> eligibleLeadResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        eligibleLeadResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        eligibleLeadResponse(eligibleLeadResponseTmbOneServiceResponse);
        final ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(eligibleLeadResponseTmbOneServiceResponse, HttpStatus.OK);
        when(creditCardClient.getEligibleLeads(eq("correlationID"), any(EligibleLeadRequest.class))).thenReturn(tmbOneServiceResponseEntity);

        final TmbOneServiceResponse<EligibleLeadResponse> eligibleLeadResponseTmbOneServiceResponse1 = new TmbOneServiceResponse<>();
        eligibleLeadResponseTmbOneServiceResponse1.setStatus(new TmbStatus("code", "message", "service", "description"));
        eligibleLeadResponse(eligibleLeadResponseTmbOneServiceResponse1);
        final ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> tmbOneServiceResponseEntity1 = new ResponseEntity<>(eligibleLeadResponseTmbOneServiceResponse1, HttpStatus.OK);
        when(creditCardClient.getEligibleLeads(any(), any())).thenReturn(tmbOneServiceResponseEntity1);


        EligibleLeadRequest requestBody = new EligibleLeadRequest();
        requestBody.setGroupAccountId("1234");
        Map<String, String> headers = new HashMap<String, String>();
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> result = eligibleLeadController.getLoanEligibleDetail(headers, requestBody);
        assertEquals(400, result.getStatusCodeValue());

    }

    private void eligibleLeadResponse(TmbOneServiceResponse<EligibleLeadResponse> eligibleLeadResponseTmbOneServiceResponse1) {
        final EligibleLeadResponse eligibleLeadResponse1 = new EligibleLeadResponse();
        final Status status1 = new Status();
        status1.setAccountStatus("accountStatus");
        status1.setContractDate("contractDate");
        eligibleLeadResponse1.setStatus(status1);
        final InstallmentPromotion installmentPromotion1 = new InstallmentPromotion();
        installmentPromotion1.setCashChillChillFlagDW("cashChillChillFlagDW");
        installmentPromotion1.setCashTransferFlagDW("cashTransferFlagDW");
        installmentPromotion1.setCashChillChillFlagAllow("cashChillChillFlagAllow");
        installmentPromotion1.setCashTransferFlagAllow("cashTransferFlagAllow");
        installmentPromotion1.setCutOfTier("cutOfTier");
        installmentPromotion1.setNormalRate("normalRate");
        installmentPromotion1.setGroupAccountId("groupAccountId");
        installmentPromotion1.setPromoSegment("promoSegment");
        installmentPromotion1.setEffectiveDate("effectiveDate");
        installmentPromotion1.setExpiryDate("expiryDate");
        eligibleLeadResponse1.setInstallmentPromotions(List.of(installmentPromotion1));
        eligibleLeadResponseTmbOneServiceResponse1.setData(eligibleLeadResponse1);
    }


    @Test
    void getFailedResponse() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        TmbOneServiceResponse<EligibleLeadResponse> serviceResponse = new TmbOneServiceResponse<>();
        serviceResponse.setStatus(getTmbStatus());
        eligibleLeadResponse(serviceResponse);
        Exception exception = new Exception("Data Not Found");
        ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> failedResponse = eligibleLeadController.getFailedResponse(responseHeaders, serviceResponse, exception);
        assertEquals(400, failedResponse.getStatusCodeValue());
    }
}

