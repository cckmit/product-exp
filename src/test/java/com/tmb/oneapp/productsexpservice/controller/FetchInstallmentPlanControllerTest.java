package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class FetchInstallmentPlanControllerTest {
    @Mock
    CreditCardClient creditCardClient;
    @InjectMocks
    FetchInstallmentPlanController fetchInstallmentPlanController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fetchInstallmentPlanController = new FetchInstallmentPlanController(creditCardClient);
    }

    @Test
    void testInstallmentPlanListSuccess()  {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        TmbOneServiceResponse<List<InstallmentPlan>> oneServiceResponse = new TmbOneServiceResponse();
        InstallmentPlan plan = new InstallmentPlan();
        plan.setInstallmentsPlan("IPP001");
        plan.setPlanDesc("3 MONTHS IPP - 0%");
        plan.setPaymentTerm("3");
        plan.setMerchantNo("");
        plan.setInterestRate("0");
        plan.setChannel("01");
        plan.setPlanStatus("01");
        List<InstallmentPlan> list = new ArrayList<>();
        list.add(plan);
        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> response = new ResponseEntity<>(
                oneServiceResponse, HttpStatus.OK);
        when(creditCardClient.getInstallmentPlan(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> InstallmentPlanRes = fetchInstallmentPlanController
                .getInstallmentPlan(correlationId);
        assertEquals(200, InstallmentPlanRes.getStatusCodeValue());

    }

    @Test
    void testInstallmentPlanListSuccessNull()  {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> response = null;
        when(creditCardClient.getInstallmentPlan(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> InstallmentPlanRes = fetchInstallmentPlanController
                .getInstallmentPlan(correlationId);
        assertEquals(400, InstallmentPlanRes.getStatusCodeValue());

    }

    @Test
    void testInstallmentPlanListError()  {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        when(creditCardClient.getInstallmentPlan(anyString())).thenThrow(RuntimeException.class);
        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> InstallmentPlanRes = fetchInstallmentPlanController
                .getInstallmentPlan(correlationId);
        assertNull(InstallmentPlanRes.getBody().getData());
    }
}

