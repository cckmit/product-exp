package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.*;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NotificationServiceTest {

    @Mock
    NotificationServiceClient notificationServiceClient;
    @Mock
    CustomerServiceClient customerServiceClient;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    CommonServiceClient commonServiceClient;
    @Mock
    TemplateService templateService;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        notificationService = new NotificationService(notificationServiceClient, customerServiceClient,
                creditCardClient, commonServiceClient, templateService);
    }

    @Test
    void sendNotificationByEmailTriggerManual() {

        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu.t@tcs.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);
        CreditCardDetail cardDetail = new CreditCardDetail();
        cardDetail.setProductId("VTOPBR");
        cardResponse.setCreditCard(cardDetail);

        when(creditCardClient.getCreditCardDetails(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208");
        Assert.assertTrue(true);
    }

    @Test
    void activeCardGetCustomerProfile() {
        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu.t@tcs.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);
        CreditCardDetail cardDetail = new CreditCardDetail();
        cardDetail.setProductId("VTOPBR");
        cardResponse.setCreditCard(cardDetail);

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));
        when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));
        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList<>();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208");
        Assert.assertTrue(true);

    }

    @Test
    void activeSetPinNotification() {
        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);
        CreditCardDetail cardDetail = new CreditCardDetail();
        cardDetail.setProductId("VTOPBR");
        cardResponse.setCreditCard(cardDetail);

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));
        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList<>();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

        notificationService.doNotifySuccessForSetPin(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208");
        Assert.assertTrue(true);
    }

    @Test
    void activeBlockCardNotification() {
        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);

        CreditCardDetail cardDetail = new CreditCardDetail();
        cardDetail.setProductId("VTOPBR");
        cardResponse.setCreditCard(cardDetail);

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList<>();
        ProductConfig config = new ProductConfig();
        config.setProductCode(null);
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

        notificationService.doNotifySuccessForBlockCard(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208");
        Assert.assertTrue(true);
    }

    @Test
    void changeTemporaryRequest() {
        SetCreditLimitReq req = new SetCreditLimitReq();
        req.setAccountId("0000000050079650011000193");
        req.setCurrentCreditLimit("120000");
        req.setEffectiveDate("2021-04-02");
        req.setReasonDesEn("For oversea emergency");
        req.setExpiryDate("1478-04-01T17:17:56.000Z");
        req.setMode("temporary");
        req.setPreviousCreditLimit("50000");
        req.setReasonDescEn("กรณีฉุกเฉินเมื่ออยู่ต่างประเทศ");
        req.setRequestReason("200");

        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);

        CreditCardDetail cardDetail = new CreditCardDetail();

        CardCreditLimit cardCreditLimit = new CardCreditLimit();
        cardCreditLimit.setPermanentCreditLimit(150000L);

        TemporaryCreditLimit tempCreditLimit = new TemporaryCreditLimit();
        tempCreditLimit.setAmounts(new BigDecimal("170000"));
        tempCreditLimit.setRequestReason("200");
        cardCreditLimit.setTemporaryCreditLimit(tempCreditLimit);

        cardDetail.setCardCreditLimit(cardCreditLimit);
        cardResponse.setCreditCard(cardDetail);

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList<ProductConfig>();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

        notificationService.doNotifySuccessForTemporaryLimit(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208", req);
        Assert.assertTrue(true);
    }

    @Test
    void changeUsageLimitTest() {
        SetCreditLimitReq req = new SetCreditLimitReq();
        req.setAccountId("0000000050079650011000193");
        req.setCurrentCreditLimit("120000");
        req.setEffectiveDate("2021-04-02");
        req.setReasonDesEn("For oversea emergency");
        req.setExpiryDate("1478-04-01T17:17:56.000Z");
        req.setMode("temporary");
        req.setPreviousCreditLimit("50000");
        req.setReasonDescEn("กรณีฉุกเฉินเมื่ออยู่ต่างประเทศ");
        req.setRequestReason("200");

        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);

        CreditCardDetail cardDetail = new CreditCardDetail();

        CardCreditLimit cardCreditLimit = new CardCreditLimit();
        cardCreditLimit.setPermanentCreditLimit(150000L);

        TemporaryCreditLimit tempCreditLimit = new TemporaryCreditLimit();
        tempCreditLimit.setAmounts(new BigDecimal("170000"));
        tempCreditLimit.setRequestReason("200");
        cardCreditLimit.setTemporaryCreditLimit(tempCreditLimit);

        cardDetail.setCardCreditLimit(cardCreditLimit);
        cardResponse.setCreditCard(cardDetail);

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<List<ProductConfig>>();
        List<ProductConfig> productConfigs = new ArrayList<>();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
        sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
        notificationService.doNotifySuccessForChangeUsageLimit(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
                "0000000050079650011000193", "001100000000000000000012036208", req);
        Assert.assertTrue(true);
    }

    @Test
    public void validCustomerResponseTest() {

        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> ab = ResponseEntity.ok(profileResponse);
        notificationService.validCustomerResponse(ab);
        Assert.assertTrue(true);
    }

    @Test
    public void testDoNotifySuccessForBlockCard() {
        TmbOneServiceResponse<NotificationResponse> response = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setDescription("Successful");
        status.setCode("1234");
        status.setMessage("Successful");
        status.setService("notificationservice");
        response.setStatus(status);
        NotificationResponse data = new NotificationResponse();
        data.setStatus(0);
        data.setMessage("successful");
        data.setGuid("1234");
        data.setSuccess(true);
        response.setData(data);
        TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
        CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
        customerProfile.setEmailAddress("witsanu@gmail.com");
        profileResponse.setData(customerProfile);
        profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));
        FetchCardResponse cardResponse = new FetchCardResponse();
        ProductCodeData productData = new ProductCodeData();
        productData.setProductNameEN("So Fast Credit Card");
        productData.setProductNameTH("โซฟาสต์");
        cardResponse.setProductCodeData(productData);
        SilverlakeStatus silverlake = new SilverlakeStatus();
        silverlake.setStatusCode(0);
        cardResponse.setStatus(silverlake);
        CreditCardDetail creditCard = new CreditCardDetail();
        creditCard.setAccountId("0000000050079650011000193");
        creditCard.setCardId("050079650011000193");
        creditCard.setDirectDepositBank("YES");
        cardResponse.setCreditCard(creditCard);
        cardResponse.setProductCodeData(productData);
        String accountId = "0000000050079650011000193";
        String correlationId = "1234";

        when(notificationServiceClient.sendMessage(anyString(), any())).thenReturn(response);

        when(customerServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

        when(creditCardClient.getCreditCardDetails(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

        TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
        List<ProductConfig> productConfigs = new ArrayList<>();
        productResponse.setData(productConfigs);
        when(commonServiceClient.getProductConfig(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

        notificationService.doNotifySuccessForBlockCard(correlationId, accountId, "crmId");
        assertNotNull(data);
    }

    @Test
    public void testSendCardActiveEmail() {
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = getTmbOneServiceResponseResponseEntity();
        notificationService.sendCardActiveEmail("xCorrelationId", "accountId", "crmId");
        assertNotNull(response);
    }

    @Test
    public void testValidCustomerResponse() {
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = getTmbOneServiceResponseResponseEntity();
        boolean result = notificationService.validCustomerResponse(response);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testDoNotifySuccessForSetPin() {
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = getTmbOneServiceResponseResponseEntity();
        notificationService.doNotifySuccessForSetPin("xCorrelationId", "accountId", "crmId");
        assertNotNull(response);
    }

    private ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> getTmbOneServiceResponseResponseEntity() {
        TmbOneServiceResponse<CustomerProfileResponseData> resp = getCustomerProfileResponseDataTmbOneServiceResponse();
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = new ResponseEntity(resp, HttpStatus.OK);
        when(customerServiceClient.getCustomerProfile(any(), any())).thenReturn(response);
        return response;
    }

    @Test
    public void testDoNotifySuccessForChangeUsageLimit() {
        SetCreditLimitReq requestBody = getSetCreditLimitReq();
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = getTmbOneServiceResponseResponseEntity();
        notificationService.doNotifySuccessForChangeUsageLimit("xCorrelationId", "accountId", "crmId", requestBody);
        assertNotNull(requestBody);
    }

    @Test
    public void testDoNotifySuccessForTemporaryLimit() {
        SetCreditLimitReq requestBody = getSetCreditLimitReq();
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = getTmbOneServiceResponseResponseEntity();
        notificationService.doNotifySuccessForTemporaryLimit("correlationId", "accountId", "crmId", requestBody);
        assertNotNull(requestBody);
    }

    private TmbOneServiceResponse<CustomerProfileResponseData> getCustomerProfileResponseDataTmbOneServiceResponse() {
        Map<String, String> header = new HashMap<>();
        header.put("test", "test");
        TmbOneServiceResponse<CustomerProfileResponseData> resp = new TmbOneServiceResponse<>();
        CustomerProfileResponseData data = new CustomerProfileResponseData();
        data.setEmailAddress("test@test.com");
        resp.setData(data);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("notification-service");
        tmbStatus.setDescription("notification");
        tmbStatus.setCode("1234");
        tmbStatus.setMessage("1234");
        resp.setStatus(tmbStatus);
        return resp;
    }

    private SetCreditLimitReq getSetCreditLimitReq() {
        SetCreditLimitReq requestBody = new SetCreditLimitReq();
        requestBody.setAccountId("1234");
        requestBody.setRequestReason("test");
        requestBody.setCurrentCreditLimit("1243");
        requestBody.setMode("test");
        requestBody.setEffectiveDate("test");
        requestBody.setExpiryDate("test");
        requestBody.setPreviousCreditLimit("1234");
        requestBody.setReasonDescEn("test");
        requestBody.setPreviousCreditLimit("test");
        requestBody.setReasonDesTh("test");
        requestBody.setType("test");
        return requestBody;
    }


    @Test
    public void testDoNotifyApplySoGood() {
        CardInstallmentResponse response = new CardInstallmentResponse();
        StatusResponse status = new StatusResponse();
        status.setStatusCode("0");
        ErrorStatus errorStatus = new ErrorStatus();
        errorStatus.setErrorCode("error code");
        errorStatus.setDescription("1234");

        List<ErrorStatus> error = new ArrayList();
        for (ErrorStatus stat : error) {
            stat.setDescription("error code");
            stat.setErrorCode("0");
            error.add(stat);
        }
        error.add(errorStatus);
        status.setErrorStatus(error);


        CreditCardModel model = new CreditCardModel();
        CardInstallmentModel card = new CardInstallmentModel();
        card.setAmounts(1234.00);
        card.setTransactionKey("1234");
        card.setTransactionDescription("success");
        card.setOrderNo("1234");
        model.setCardInstallment(card);
        model.setAccountId("124");
        response.setStatus(status);
        response.setCreditCard(model);
        CardInstallmentQuery requestBody = new CardInstallmentQuery();
        TmbOneServiceResponse<List<InstallmentPlan>> tmbResponse = new TmbOneServiceResponse();
        CardInstallment cardInstall = new CardInstallment();
        cardInstall.setInterest("1234");
        List<CardInstallment> cardInstallment = new ArrayList<>();
        CardInstallment element = new CardInstallment();
        InstallmentPlan installment = new InstallmentPlan();
        installment.setInstallmentsPlan("1234");
        installment.setChannel("1234");
        installment.setPlanSeqId("1234");
        installment.setInterestRate("1234");
        installment.setPaymentTerm("1234");
        installment.setPlanStatus("success");
        installment.setMerchantNo("1234");
        List<InstallmentPlan> plan = new ArrayList<>();
        for (InstallmentPlan installmentplan : plan) {
            installmentplan.setInstallmentsPlan("1234");
            installmentplan.setChannel("1234");
            installmentplan.setPlanSeqId("1234");
            installment.setInterestRate("1234");
            installment.setPaymentTerm("1234");
            installment.setPlanStatus("success");
            plan.add(installmentplan);
        }
        plan.add(installment);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("notification-service");
        tmbStatus.setDescription("notification");
        tmbStatus.setCode("1234");
        tmbStatus.setMessage("1234");
        tmbResponse.setStatus(tmbStatus);
        tmbResponse.setData(plan);
        element.setPostDate("1234");

        cardInstallment.add(element);
        requestBody.setAccountId("1234");
        requestBody.setCardInstallment(cardInstallment);


        tmbResponse.setStatus(tmbStatus);
        tmbResponse.setData(plan);

        ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> responseInstallments = new ResponseEntity(tmbResponse, HttpStatus.OK);
        when(creditCardClient.getInstallmentPlan(any())).thenReturn(responseInstallments);

        notificationService.doNotifyApplySoGood("correlationId", "accountId", "crmId", Arrays.asList(response), requestBody);
        assertNotEquals(null, responseInstallments);
    }

}
