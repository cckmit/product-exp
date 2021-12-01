package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.dropdown.CommonCodeEntry;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.loan.MaxMinLoanSubmission;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionUpdateFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.AccountSaving;
import com.tmb.oneapp.productsexpservice.model.loan.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.*;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanCustomerServiceTest {
    @Mock
    private LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    @Mock
    private LoanSubmissionUpdateFacilityInfoClient updateFacilityInfoClient;
    @Mock
    private LoanSubmissionGetDropdownListClient getDropdownListClient;
    @Mock
    private CustomerExpServiceClient customerExpServiceClient;
    @Mock
    private CommonServiceClient commonServiceClient;

    LoanCustomerService loanCustomerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanCustomerService = new LoanCustomerService(getFacilityInfoClient, updateFacilityInfoClient, getDropdownListClient, customerExpServiceClient, commonServiceClient);
    }

    @Test
    public void testSaveCustomerProfileService() throws Exception {
        Facility[] facilities = {mockFacility()};
        ResponseFacility respFacility = new ResponseFacility();
        Body body = new Body();
        body.setFacilities(facilities);
        respFacility.setBody(body);

        com.tmb.common.model.legacy.rsl.ws.facility.response.Header header = new com.tmb.common.model.legacy.rsl.ws.facility.response.Header();
        header.setChannel("MIB");
        header.setModule("3");
        header.setResponseCode("MSG_000");
        header.setRequestID(UUID.randomUUID().toString());
        respFacility.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility responseFacility = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility();
        com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header header1 = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header();
        header1.setResponseCode("MSG_000");
        responseFacility.setHeader(header1);

        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(mockCommonList()));
        when(updateFacilityInfoClient.updateFacilityInfo(any())).thenReturn(responseFacility);
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(mockDropdownList());
        when(customerExpServiceClient.getCustomerAccountSaving(any(), any())).thenReturn(mockAccountSaving());

        LoanCustomerSubmissionRequest request = new LoanCustomerSubmissionRequest();
        request.setCaID(1L);
        request.setFeatureType("S");
        LoanCustomerSubmissionResponse response = loanCustomerService.saveCustomerSubmission(request);

        Assert.assertNotNull(response);
    }

    @Test
    public void testSaveCustomerProfileServiceFail() throws Exception {
        Facility[] facilities = {mockFacility()};
        ResponseFacility respFacility = new ResponseFacility();
        Body body = new Body();
        body.setFacilities(facilities);
        respFacility.setBody(body);

        com.tmb.common.model.legacy.rsl.ws.facility.response.Header header = new com.tmb.common.model.legacy.rsl.ws.facility.response.Header();
        header.setChannel("MIB");
        header.setModule("3");
        header.setResponseCode("MSG_000");
        header.setRequestID(UUID.randomUUID().toString());
        respFacility.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility responseFacility = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility();
        com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header header1 = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header();
        header1.setResponseCode("MSG_999");
        responseFacility.setHeader(header1);

        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(mockCommonList()));
        when(updateFacilityInfoClient.updateFacilityInfo(any())).thenReturn(responseFacility);
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(mockDropdownList());
        when(customerExpServiceClient.getCustomerAccountSaving(any(), any())).thenReturn(mockAccountSaving());

        LoanCustomerSubmissionRequest request = new LoanCustomerSubmissionRequest();
        request.setCaID(1L);
        request.setFeatureType("S");

        assertThrows(TMBCommonException.class, () ->
                loanCustomerService.saveCustomerSubmission(request)
        );

    }

    @Test
    public void testGetCustomerProfileService() throws Exception {
        Facility[] facilities = new Facility[1];
        facilities[0] = mockFacility();
        ResponseFacility respFacility = new ResponseFacility();
        Body body = new Body();
        body.setFacilities(facilities);
        respFacility.setBody(body);

        com.tmb.common.model.legacy.rsl.ws.facility.response.Header header = new com.tmb.common.model.legacy.rsl.ws.facility.response.Header();
        header.setChannel("MIB");
        header.setModule("3");
        header.setResponseCode("MSG_000");
        header.setRequestID(UUID.randomUUID().toString());
        respFacility.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility responseFacility = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility();
        com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header header1 = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header();
        header1.setResponseCode("MSG_000");
        responseFacility.setHeader(header1);

        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(mockCommonList()));
        when(updateFacilityInfoClient.updateFacilityInfo(any())).thenReturn(responseFacility);
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(mockDropdownList());
        when(customerExpServiceClient.getCustomerAccountSaving(any(), any())).thenReturn(mockAccountSaving());

        LoanCustomerRequest request = new LoanCustomerRequest();
        request.setCaId(2021082304188823L);
        LoanCustomerResponse response = loanCustomerService.getCustomerProfile("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", request, "001100000000000000000018593707");

        Assertions.assertNotNull(response);
    }

    private TmbOneServiceResponse<List<CommonData>> mockCommonList() {
        TmbOneServiceResponse<List<CommonData>> list = new TmbOneServiceResponse<List<CommonData>>();
        List<CommonData> listDatas = new ArrayList<CommonData>();
        CommonData cData = new CommonData();
        List<MaxMinLoanSubmission> listMaxMin = new ArrayList<>();
        MaxMinLoanSubmission maxMinLoanSubmission = new MaxMinLoanSubmission();
        maxMinLoanSubmission.setMax("500");
        maxMinLoanSubmission.setMin("200");
        listMaxMin.add(maxMinLoanSubmission);
        cData.setMaxMinLoanday1Loansubmission(listMaxMin);
        listDatas.add(cData);
        list.setData(listDatas);
        return list;
    }

    private Facility mockFacility() {
        Facility facility = new Facility();
        Feature feature = new Feature();
        Pricing p = new Pricing();
        LoanCustomerResponse loanCustomerResponse = new LoanCustomerResponse();
        facility.setFeatureType("S");
        facility.setFeature(feature);

        p.setRateVaraince(BigDecimal.TEN);
        Pricing[] pricings = new Pricing[2];
        pricings[0] = p;
        pricings[1] = p;

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        pricings[0].setMonthTo(BigDecimal.ONE);
        pricings[0].setMonthFrom(BigDecimal.ONE);
        pricings[0].setRateVaraince(BigDecimal.ONE);
        pricings[0].setYearFrom(BigDecimal.ONE);
        pricings[0].setYearTo(BigDecimal.ONE);
        pricings[0].setPricingType("C");
        pricings[0].setCalculatedRate(new BigDecimal(1));

        pricings[1].setMonthTo(BigDecimal.ONE);
        pricings[1].setMonthFrom(BigDecimal.ONE);
        pricings[1].setRateVaraince(BigDecimal.ONE);
        pricings[1].setYearFrom(BigDecimal.ONE);
        pricings[1].setYearTo(BigDecimal.ONE);
        pricings[1].setPricingType("S");
        pricings[1].setCalculatedRate(new BigDecimal(1));

        facility.setPricings(pricings);
        LoanCustomerPricing customerPricing = new LoanCustomerPricing();
        customerPricing.setYearFrom(BigDecimal.ONE);
        customerPricing.setYearTo(BigDecimal.ONE);
        customerPricing.setMonthFrom(BigDecimal.ONE);
        customerPricing.setMonthTo(BigDecimal.ONE);
        customerPricing.setRate("12");
        customerPricing.setRateVariance(BigDecimal.ONE);
        pricingList.add(customerPricing);
        loanCustomerResponse.setPricings(pricingList);

        facility.setFeatureType("S");

        return facility;
    }

    private ResponseDropdown mockDropdownList() {
        ResponseDropdown response = new ResponseDropdown();
        com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body body = new com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body();
        CommonCodeEntry commonCodeEntry = new CommonCodeEntry();
        CommonCodeEntry[] commonCodeEntries = {commonCodeEntry};
        body.setCommonCodeEntries(commonCodeEntries);
        response.setBody(body);
        return response;
    }

    private ResponseEntity<TmbOneServiceResponse<AccountSaving>> mockAccountSaving() {
        TmbOneServiceResponse<AccountSaving> tmbResponse = new TmbOneServiceResponse<>();
        AccountSaving accountSaving = new AccountSaving();
        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setAccountNumber("accountNo");
        depositAccount.setProductNameTh("accountName");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAllowReceiveLoanFund("1");
        depositAccount.setAllowPayLoanDirectDebit("1");
        depositAccount.setRelationshipCode("PRIIND");
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(depositAccount);
        accountSaving.setDepositAccountLists(depositAccountList);
        tmbResponse.setData(accountSaving);
        return ResponseEntity.ok().body(tmbResponse);
    }

}

