package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.facility.ApprovalMemoFacility;
import com.tmb.common.model.legacy.rsl.common.ob.creditcard.CreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.common.ob.individual.Individual;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Header;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.submit.response.ResponseInstantLoanSubmit;
import com.tmb.common.model.response.notification.NotificationResponse;
import com.tmb.oneapp.productsexpservice.constant.LegacyResponseCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.RSLProductCodeEnum;
import com.tmb.oneapp.productsexpservice.feignclients.SFTPClientImp;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.*;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.FlexiLoanConfirmRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class FlexiLoanConfirmServiceTest {
    @Mock
    private LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    @Mock
    private LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    @Mock
    private LoanSubmissionGetCreditCardInfoClient getCreditCardInfoClient;
    @Mock
    private LoanSubmissionGetApplicationInfoClient getApplicationInfoClient;
    @Mock
    private LoanSubmissionInstantLoanCalUWClient instantLoanCalUWClient;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LoanSubmissionInstantLoanSubmitApplicationClient submitApplicationClient;
    @Mock
    private FileGeneratorService fileGeneratorService;
    @Mock
    private SFTPClientImp sftpClientImp;

    FlexiLoanConfirmService flexiLoanConfirmService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        flexiLoanConfirmService = new FlexiLoanConfirmService(getFacilityInfoClient, getCustomerInfoClient, getCreditCardInfoClient, getApplicationInfoClient, instantLoanCalUWClient, notificationService, submitApplicationClient, fileGeneratorService, sftpClientImp);
        mockSuccess();
    }

    private void mockSuccess() throws Exception {
        doReturn(mockGetFacilityInfoSuccess()).when(getFacilityInfoClient).searchFacilityInfoByCaID(anyLong());
        doReturn(mockGetCustomerInfoSuccess()).when(getCustomerInfoClient).searchCustomerInfoByCaID(anyLong());
        doReturn(mockGetCreditCardInfoSuccess()).when(getCreditCardInfoClient).searchCreditcardInfoByCaID(anyLong());
        doReturn(mockGetApplicationInfoSuccess()).when(getApplicationInfoClient).getApplicationInfo(anyLong());
        doReturn(mockGetInstantLoanCalUWSuccess()).when(instantLoanCalUWClient).getCalculateUnderwriting(any());
        doReturn(mockSubmitInstantLoanSubmission()).when(submitApplicationClient).submitApplication(any(), any());
        doReturn(mockSendNotificationSuccess()).when(notificationService).sendNotifyFlexiLoanSubmission(anyString(), anyString(), anyString(), any());
        doNothing().when(fileGeneratorService).generateFlexiLoanSubmissionPdf(any(), anyString(), anyString());

    }

    @Test
    public void testFlexiLoanConfirmService_CreditCard_Success() throws Exception {
        FlexiLoanConfirmRequest request = mockRequest();
        request.setProductCode(RSLProductCodeEnum.CREDIT_CARD_TTB_ABSOLUTE.getProductCode());
        flexiLoanConfirmService.confirm(mockRequestHeaders(), mockRequest());
    }

    @Test
    public void testFlexiLoanConfirmService_FlashCared_Success() throws Exception {
        FlexiLoanConfirmRequest request = mockRequest();
        request.setProductCode(RSLProductCodeEnum.FLASH_CARD_PLUS.getProductCode());
        flexiLoanConfirmService.confirm(mockRequestHeaders(), mockRequest());
    }

    private Map<String, String> mockRequestHeaders() {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ProductsExpServiceConstant.X_CRMID, "x_crmid");
        requestHeaders.put(ProductsExpServiceConstant.ACCOUNT_ID, "account_id");
        requestHeaders.put(ProductsExpServiceConstant.X_CORRELATION_ID, "x_correlation_id");
        return requestHeaders;
    }

    private FlexiLoanConfirmRequest mockRequest() {
        FlexiLoanConfirmRequest request = new FlexiLoanConfirmRequest();
        request.setCaID(1L);
        request.setProductCode("RC01");
        request.setProductNameTH("แฟรชการ์ด");
        return request;
    }

    private ResponseFacility mockGetFacilityInfoSuccess() {
        ResponseFacility response = new ResponseFacility();

        Header header = new Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);

        Body body = new Body();
        Facility facility = new Facility();

        Pricing pricing = new Pricing();
        pricing.setPricingType("S");
        pricing.setRateVaraince(BigDecimal.TEN);
        Pricing[] pricingList = {pricing};
        facility.setPricings(pricingList);

        Feature feature = new Feature();
        feature.setDisbAcctNo("xxx");
        facility.setFeature(feature);

        facility.setFeatureType("S");

        Facility[] facilities = {facility};
        body.setFacilities(facilities);
        response.setBody(body);

        return response;
    }

    private ResponseIndividual mockGetCustomerInfoSuccess() {
        ResponseIndividual response = new ResponseIndividual();

        com.tmb.common.model.legacy.rsl.ws.individual.response.Header header = new com.tmb.common.model.legacy.rsl.ws.individual.response.Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.individual.response.Body body = new com.tmb.common.model.legacy.rsl.ws.individual.response.Body();
        response.setBody(body);

        Individual individual = new Individual();
        Individual[] individuals = {individual};
        response.getBody().setIndividuals(individuals);
        return response;
    }

    private ResponseCreditcard mockGetCreditCardInfoSuccess() {
        ResponseCreditcard response = new ResponseCreditcard();

        com.tmb.common.model.legacy.rsl.ws.creditcard.response.Header header = new com.tmb.common.model.legacy.rsl.ws.creditcard.response.Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);
        response.getHeader().setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());

        com.tmb.common.model.legacy.rsl.ws.creditcard.response.Body body = new com.tmb.common.model.legacy.rsl.ws.creditcard.response.Body();
        response.setBody(body);

        CreditCard creditCard = new CreditCard();
        CreditCard[] CreditCards = {creditCard};
        response.getBody().setCreditCards(CreditCards);
        return response;
    }

    private ResponseApplication mockGetApplicationInfoSuccess() {
        ResponseApplication response = new ResponseApplication();

        com.tmb.common.model.legacy.rsl.ws.application.response.Header header = new com.tmb.common.model.legacy.rsl.ws.application.response.Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.application.response.Body body = new com.tmb.common.model.legacy.rsl.ws.application.response.Body();
        body.setAppRefNo("appRefNo");
        body.setApplicationDate("2021-06-29T10:21:43.000Z");
        response.setBody(body);

        return response;
    }

    private ResponseInstantLoanCalUW mockGetInstantLoanCalUWSuccess() {
        ResponseInstantLoanCalUW response = new ResponseInstantLoanCalUW();

        com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Header header = new com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body body = new com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body();

        ApprovalMemoFacility approvalMemoFacility = mock(ApprovalMemoFacility.class);
        ApprovalMemoFacility[] approvalMemoFacilities = {approvalMemoFacility};
        body.setApprovalMemoFacilities(approvalMemoFacilities);

        body.setUnderwritingResult("APPROVE");
        response.setBody(body);

        return response;
    }

    private TmbOneServiceResponse<NotificationResponse> mockSendNotificationSuccess() {
        TmbOneServiceResponse<NotificationResponse> response = new TmbOneServiceResponse<>();
        NotificationResponse resp = new NotificationResponse();
        resp.setSuccess(true);
        response.setData(resp);
        return response;
    }

    private ResponseInstantLoanSubmit mockSubmitInstantLoanSubmission() {
        ResponseInstantLoanSubmit response = new ResponseInstantLoanSubmit();

        com.tmb.common.model.legacy.rsl.ws.instant.submit.response.Header header = new com.tmb.common.model.legacy.rsl.ws.instant.submit.response.Header();
        header.setResponseCode(LegacyResponseCodeEnum.SUCCESS.getCode());
        response.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.instant.submit.response.Body body = new com.tmb.common.model.legacy.rsl.ws.instant.submit.response.Body();
        response.setBody(body);

        return response;
    }
}
