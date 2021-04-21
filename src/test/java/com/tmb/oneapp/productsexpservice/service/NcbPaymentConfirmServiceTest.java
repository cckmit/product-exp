package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.request.notification.EmailChannel;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRecord;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class NcbPaymentConfirmServiceTest {

    private final CustomerServiceClient customerServiceClient = Mockito.mock(CustomerServiceClient.class);
    private final NotificationServiceClient notificationServiceClient = Mockito.mock(NotificationServiceClient.class);

    private final NcbPaymentConfirmService ncbPaymentConfirmService = new NcbPaymentConfirmService(
            customerServiceClient, notificationServiceClient);

    @Test
    void sendEmail_Success() {
        String crmId = "001100000000000000000099999998";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setSuccess(true);
        notificationResponse.setGuid("2a707b40-fada-445d-a8a8-1d5d43000f9f");
        notificationResponse.setMessage("Complete.");
        notificationResponse.setStatus(200);

        TmbOneServiceResponse<NotificationResponse> mockResponseSendMessage = new TmbOneServiceResponse<>();
        mockResponseSendMessage.setData(notificationResponse);

        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(mockResponseSendMessage);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("template_name", "ncb_payment_completed");
        params.put("custFullNameTH", ("กขค" + " " + "งจ"));
        params.put("custFullNameEN", ("NAME" + " " + "TEST"));
        params.put("DeliveryMethodTH", "ทางอีเมล");
        params.put("DeliveryMethodEN", "By e-mail");
        params.put("CustDeliveryDetail", "abc@tmb.com");
        params.put("toAcctNumber", "1234567890");
        params.put("successDateTime", "2021-04-02T14:32");
        params.put("ReferenceID", "1234567890");

        EmailChannel emailChannel = new EmailChannel();
        emailChannel.setCc("");
        emailChannel.setEmailEndpoint("abc@tmb.com");
        emailChannel.setEmailSearch(false);

        NotificationRecord notificationRecord = new NotificationRecord();
        notificationRecord.setCrmId(crmId);
        notificationRecord.setParams(params);
        notificationRecord.setEmail(emailChannel);

        List<NotificationRecord> notificationRecordList = new ArrayList<>();
        notificationRecordList.add(notificationRecord);

        Assert.assertTrue(ncbPaymentConfirmService.sendEmail(correlationId, notificationRecordList));
    }

    @Test
    void sendEmail_Fail() {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        when(notificationServiceClient.sendMessage(any(), any())).thenThrow(new IllegalArgumentException());

        List<NotificationRecord> notificationRecordList = new ArrayList<>();

        Assert.assertFalse(ncbPaymentConfirmService.sendEmail(correlationId, notificationRecordList));
    }

    @Test
    void createNcbCase_Success() {
        String crmId = "001100000000000000000099999998";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String firstnameTh = "NAME";
        String lastnameTh = "TEST";
        String firstnameEn = "NAME";
        String lastnameEn = "TEST";
        String deliveryMethod = "by email";

        String caseRef = "12312312";
        Map<String, String> response = new HashMap<>();
        response.put("case_number", caseRef);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenReturn(mockResponse);

        Assert.assertNotEquals(new HashMap<>(), ncbPaymentConfirmService.createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, deliveryMethod));
    }

    @Test
    void createNcbCase_Fail() {
        String crmId = "123";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String firstnameTh = "a";
        String lastnameTh = "b";
        String firstnameEn = "c";
        String lastnameEn = "d";

        String deliveryMethod = "by email";

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenThrow(new IllegalArgumentException());

        Assert.assertEquals(new HashMap<>(), ncbPaymentConfirmService.createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, deliveryMethod));
    }

    @Test
    void postFirstTimeUsage_Success() {
        String crmId = "001100000000000000000000051187";
        String deviceId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        TmbOneServiceResponse<String> mockResponseFirstTimeUsage = new TmbOneServiceResponse<>();
        mockResponseFirstTimeUsage.setData("");

        ResponseEntity<TmbOneServiceResponse<String>> mockResponse = new ResponseEntity<>(mockResponseFirstTimeUsage, HttpStatus.OK);

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenReturn(mockResponse);

        Assert.assertNotNull(ncbPaymentConfirmService.postFirstTimeUsage(crmId, deviceId,"NCBR"));
    }

    @Test
    void postFirstTimeUsage_Fail() {
        String crmId = "13513534642645";
        String deviceId = "";

        when(customerServiceClient.postFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenThrow(new IllegalArgumentException());

        Assert.assertEquals("", ncbPaymentConfirmService.postFirstTimeUsage(crmId, deviceId,"NCBR"));
    }

    @Test
    void putFirstTimeUsage_Success() {
        String crmId = "001100000000000000000000051187";
        String deviceId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        TmbOneServiceResponse<String> mockResponseFirstTimeUsage = new TmbOneServiceResponse<>();
        mockResponseFirstTimeUsage.setData("");

        ResponseEntity<TmbOneServiceResponse<String>> mockResponse = new ResponseEntity<>(mockResponseFirstTimeUsage, HttpStatus.OK);

        when(customerServiceClient.putFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenReturn(mockResponse);

        Assert.assertNotNull(ncbPaymentConfirmService.putFirstTimeUsage(crmId, deviceId,"NCBR"));
    }

    @Test
    void putFirstTimeUsage_Fail() {
        String crmId = "13513534642645";
        String deviceId = "";

        when(customerServiceClient.putFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenThrow(new IllegalArgumentException());

        Assert.assertEquals("", ncbPaymentConfirmService.putFirstTimeUsage(crmId, deviceId,"NCBR"));
    }

    @Test
    void confirmNcbPayment_Email_Success() throws TMBCommonException {
        String crmId = "001100000000000000000099999998";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setSuccess(true);
        notificationResponse.setGuid("2a707b40-fada-445d-a8a8-1d5d43000f9f");
        notificationResponse.setMessage("Complete.");
        notificationResponse.setStatus(200);

        TmbOneServiceResponse<NotificationResponse> mockResponseSendMessage = new TmbOneServiceResponse<>();
        mockResponseSendMessage.setData(notificationResponse);

        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(mockResponseSendMessage);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("template_name", "ncb_payment_completed");
        params.put("custFullNameTH", ("กขค" + " " + "งจ"));
        params.put("custFullNameEN", ("NAME" + " " + "TEST"));
        params.put("DeliveryMethodTH", "ทางอีเมล");
        params.put("DeliveryMethodEN", "By e-mail");
        params.put("CustDeliveryDetail", "abc@tmb.com");
        params.put("toAcctNumber", "1234567890");
        params.put("successDateTime", "2021-04-02T14:32");
        params.put("ReferenceID", "1234567890");

        EmailChannel emailChannel = new EmailChannel();
        emailChannel.setCc("");
        emailChannel.setEmailEndpoint("abc@tmb.com");
        emailChannel.setEmailSearch(false);

        NotificationRecord notificationRecord = new NotificationRecord();
        notificationRecord.setCrmId(crmId);
        notificationRecord.setParams(params);
        notificationRecord.setEmail(emailChannel);

        List<NotificationRecord> notificationRecordList = new ArrayList<>();
        notificationRecordList.add(notificationRecord);

        String caseRef = "12312312";
        Map<String, String> response = new HashMap<>();
        response.put("case_number", caseRef);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenReturn(mockResponse);

        CustomerFirstUsage customerFirstUsage = new CustomerFirstUsage();
        customerFirstUsage.setCrmId(crmId);
        customerFirstUsage.setDeviceId("2a707b40-fada-445d-a8a8-1d5d43000f9f");
        customerFirstUsage.setServiceTypeId("NCBR");
        customerFirstUsage.setTimestamp("2021-04-02T14:32");

        TmbOneServiceResponse<CustomerFirstUsage> responseFirstTimeUsage = new TmbOneServiceResponse<>();
        responseFirstTimeUsage.setData(customerFirstUsage);

        ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> mockResponseFirstTimeUsage = new ResponseEntity<>(responseFirstTimeUsage, HttpStatus.OK);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenReturn(mockResponseFirstTimeUsage);

        when(customerServiceClient.putFirstTimeUsage(anyString(), anyString(), eq("NCBR")));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", correlationId);
        header.put("x-crmid", crmId);
        header.put("device-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");

        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = ncbPaymentConfirmService.confirmNcbPayment(header, "NCBR", "กขค","งจ", "NAME", "TEST", "abc@tmb.com", "123/12 asdfwaefawef", "email", "1234567890");
        Assert.assertNotNull(ncbPaymentConfirmResponse);
    }

    @Test
    void confirmNcbPayment_Post_Success() throws TMBCommonException {
        String crmId = "001100000000000000000099999998";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setSuccess(true);
        notificationResponse.setGuid("2a707b40-fada-445d-a8a8-1d5d43000f9f");
        notificationResponse.setMessage("Complete.");
        notificationResponse.setStatus(200);

        TmbOneServiceResponse<NotificationResponse> mockResponseSendMessage = new TmbOneServiceResponse<>();
        mockResponseSendMessage.setData(notificationResponse);

        when(notificationServiceClient.sendMessage(any(), any())).thenReturn(mockResponseSendMessage);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("template_name", "ncb_payment_completed");
        params.put("custFullNameTH", ("กขค" + " " + "งจ"));
        params.put("custFullNameEN", ("NAME" + " " + "TEST"));
        params.put("DeliveryMethodTH", "ทางไปรษณีย์");
        params.put("DeliveryMethodEN", "By post");
        params.put("CustDeliveryDetail", "123/12 awfawefawefas");
        params.put("toAcctNumber", "1234567890");
        params.put("successDateTime", "2021-04-02T14:32");
        params.put("ReferenceID", "1234567890");

        EmailChannel emailChannel = new EmailChannel();
        emailChannel.setCc("");
        emailChannel.setEmailEndpoint("abc@tmb.com");
        emailChannel.setEmailSearch(false);

        NotificationRecord notificationRecord = new NotificationRecord();
        notificationRecord.setCrmId(crmId);
        notificationRecord.setParams(params);
        notificationRecord.setEmail(emailChannel);

        List<NotificationRecord> notificationRecordList = new ArrayList<>();
        notificationRecordList.add(notificationRecord);

        String caseRef = "12312312";
        Map<String, String> response = new HashMap<>();
        response.put("case_number", caseRef);

        TmbOneServiceResponse<Map<String, String>> mockResponseCaseSubmit = new TmbOneServiceResponse<>();
        mockResponseCaseSubmit.setData(response);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> mockResponse = new ResponseEntity<>(mockResponseCaseSubmit, HttpStatus.OK);

        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenReturn(mockResponse);

        CustomerFirstUsage customerFirstUsage = new CustomerFirstUsage();
        customerFirstUsage.setCrmId(crmId);
        customerFirstUsage.setDeviceId("2a707b40-fada-445d-a8a8-1d5d43000f9f");
        customerFirstUsage.setServiceTypeId("NCBR");
        customerFirstUsage.setTimestamp("2021-04-02T14:32");

        TmbOneServiceResponse<CustomerFirstUsage> responseFirstTimeUsage = new TmbOneServiceResponse<>();
        responseFirstTimeUsage.setData(customerFirstUsage);

        ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> mockResponseFirstTimeUsage = new ResponseEntity<>(responseFirstTimeUsage, HttpStatus.OK);

        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenReturn(mockResponseFirstTimeUsage);

        when(customerServiceClient.putFirstTimeUsage(anyString(), anyString(), eq("NCBR")));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", correlationId);
        header.put("x-crmid", crmId);
        header.put("device-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");

        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = ncbPaymentConfirmService.confirmNcbPayment(header, "NCBR", "กขค","งจ", "NAME", "TEST", "abc@tmb.com", "123/12 asdfwaefawef", "post", "1234567890");
        Assert.assertNotNull(ncbPaymentConfirmResponse);
    }

    @Test
    void confirmNcbPayment_Fail() throws TMBCommonException {
        String crmId = "";
        String correlationId = "";

        when(notificationServiceClient.sendMessage(any(), any())).thenThrow(new IllegalArgumentException());
        when(customerServiceClient.submitNcbCustomerCase(any(), any(), any(), any(), any())).thenThrow(new IllegalArgumentException());
        when(customerServiceClient.getFirstTimeUsage(anyString(), anyString(), eq("NCBR"))).thenThrow(new IllegalArgumentException());
        when(customerServiceClient.putFirstTimeUsage(anyString(), anyString(), eq("NCBR")));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", correlationId);
        header.put("x-crmid", crmId);
        header.put("device-id", "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");

        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = ncbPaymentConfirmService.confirmNcbPayment(header, "NCBR", "","", "NAME", "TEST", "abc@tmb.com", "123/12 asdfwaefawef", "email", "1234567890");
        Assert.assertNotNull(ncbPaymentConfirmResponse);
    }
}