package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
        requestBody.setCashTransferFlag("Y");
        ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> result = installmentRateController.getLoanAccountDetail(correlationId, requestBody);
        Assert.assertNotEquals(400, result.getStatusCodeValue());
    }

    @Test
    public void testTestGetLoanAccountDetail() {
        // Setup

        // Configure CreditCardClient.getInstallmentRate(...).
        final TmbOneServiceResponse<InstallmentRateResponse> installmentRateResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        installmentRateResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final InstallmentRateResponse installmentRateResponse = new InstallmentRateResponse();
        final Status status = new Status();
        status.setAccountStatus("accountStatus");
        status.setContractDate("contractDate");
        installmentRateResponse.setStatus(status);
        final InstallmentData installmentData = new InstallmentData();
        installmentData.setProductGroup("productGroup");
        installmentData.setSegment("segment");
        installmentData.setCashChillChillModel("cashChillChillModel");
        installmentData.setCashTransferModel("cashTransferModel");
        final ModelTenor modelTenor = new ModelTenor();
        modelTenor.setPricingModel("pricingModel");
        modelTenor.setModelType("modelType");
        modelTenor.setTenor("tenor");
        modelTenor.setFlagRateType("flagRateType");
        final PricingTier pricingTier = new PricingTier();
        pricingTier.setTier("tier");
        pricingTier.setMinTenor("minTenor");
        pricingTier.setMaxTenor("maxTenor");
        pricingTier.setRate("rate");
        modelTenor.setPricingTiers(List.of(pricingTier));
        modelTenor.setPrincipleAmount("principleAmount");
        modelTenor.setTotalInterestAmount("totalInterestAmount");
        modelTenor.setTotalAmount("totalAmount");
        modelTenor.setFirstMonthAmount("firstMonthAmount");
        installmentData.setModelTenors(List.of(modelTenor));
        installmentRateResponse.setInstallmentData(installmentData);
        installmentRateResponseTmbOneServiceResponse.setData(installmentRateResponse);
        final ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(installmentRateResponseTmbOneServiceResponse, HttpStatus.OK);
        when(creditCardClient.getInstallmentRate(eq("correlationID"), any(InstallmentRateRequest.class))).thenReturn(tmbOneServiceResponseEntity);

        // Configure InstallmentRateController.getLoanAccountDetail(...).
        final TmbOneServiceResponse<InstallmentRateResponse> installmentRateResponseTmbOneServiceResponse1 = new TmbOneServiceResponse<>();
        installmentRateResponseTmbOneServiceResponse1.setStatus(new TmbStatus("code", "message", "service", "description"));
        final InstallmentRateResponse installmentRateResponse1 = new InstallmentRateResponse();
        final Status status1 = new Status();
        status1.setAccountStatus("accountStatus");
        status1.setContractDate("contractDate");
        installmentRateResponse1.setStatus(status1);
        final InstallmentData installmentData1 = new InstallmentData();
        installmentData1.setProductGroup("productGroup");
        installmentData1.setSegment("segment");
        installmentData1.setCashChillChillModel("cashChillChillModel");
        installmentData1.setCashTransferModel("cashTransferModel");
        final ModelTenor modelTenor1 = new ModelTenor();
        modelTenor1.setPricingModel("pricingModel");
        modelTenor1.setModelType("modelType");
        modelTenor1.setTenor("tenor");
        modelTenor1.setFlagRateType("flagRateType");
        final PricingTier pricingTier1 = new PricingTier();
        pricingTier1.setTier("tier");
        pricingTier1.setMinTenor("minTenor");
        pricingTier1.setMaxTenor("maxTenor");
        pricingTier1.setRate("rate");
        modelTenor1.setPricingTiers(List.of(pricingTier1));
        modelTenor1.setPrincipleAmount("principleAmount");
        modelTenor1.setTotalInterestAmount("totalInterestAmount");
        modelTenor1.setTotalAmount("totalAmount");
        modelTenor1.setFirstMonthAmount("firstMonthAmount");
        installmentData1.setModelTenors(List.of(modelTenor1));
        installmentRateResponse1.setInstallmentData(installmentData1);
        installmentRateResponseTmbOneServiceResponse1.setData(installmentRateResponse1);
        final ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> tmbOneServiceResponseEntity1 = new ResponseEntity<>(installmentRateResponseTmbOneServiceResponse1, HttpStatus.OK);
        String correlationId="c83936c284cb398fA46CF16F399C";

        InstallmentRateRequest requestBody = new InstallmentRateRequest();
        requestBody.setAmount("1234.00");
        requestBody.setGetAllDetailFlag("Y");
        requestBody.setGroupAccountId("0000000050080760015");
        requestBody.setDisbursementDate("2020-10-16");
        requestBody.setBillCycleCutDate("3");
        requestBody.setPromoSegment("CS7");
        requestBody.setCashChillChillFlag("Y");
        requestBody.setGetAllDetailFlag("N");
        requestBody.setCashTransferFlag("Y");

        // Run the test

        ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> loanAccountDetail = installmentRateController.getLoanAccountDetail(correlationId, requestBody);

        // Verify the results
        Assert.assertEquals(400, loanAccountDetail.getStatusCodeValue());

    }
}

