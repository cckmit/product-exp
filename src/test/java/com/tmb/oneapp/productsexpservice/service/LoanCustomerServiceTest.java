package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.dropdown.CommonCodeEntry;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionUpdateFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.AccountSaving;
import com.tmb.oneapp.productsexpservice.model.loan.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerSubmissionResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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

    LoanCustomerService loanCustomerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanCustomerService = new LoanCustomerService(getFacilityInfoClient, updateFacilityInfoClient, getDropdownListClient, customerExpServiceClient);
    }

    @Test
    public void testGetCustomerProfileService() throws ServiceException, RemoteException, JsonProcessingException {
        Facility[] facilities = {mockFacility()};
        ResponseFacility respFacility = new ResponseFacility();
        Body body = new Body();
        body.setFacilities(facilities);
        respFacility.setBody(body);
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(mockDropdownList());
        when(customerExpServiceClient.getCustomerAccountSaving(any(), any())).thenReturn(mockAccountSaving());

        LoanCustomerRequest request = new LoanCustomerRequest();
        request.setCaID(1L);
        LoanCustomerResponse response = loanCustomerService.getCustomerProfile("xxx", request);
        Assert.assertNotNull(response);
    }

    @Test
    public void testSaveCustomerProfileService() throws ServiceException, RemoteException, JsonProcessingException {
        Facility[] facilities = {mockFacility()};
        ResponseFacility respFacility = new ResponseFacility();
        Body body = new Body();
        body.setFacilities(facilities);
        respFacility.setBody(body);
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(mockDropdownList());
        when(customerExpServiceClient.getCustomerAccountSaving(any(), any())).thenReturn(mockAccountSaving());

        LoanCustomerSubmissionRequest request = new LoanCustomerSubmissionRequest();
        request.setCaID(1L);
        request.setFeatureType("S");
        LoanCustomerSubmissionResponse response = loanCustomerService.saveCustomerSubmission(request);
        Assert.assertNotNull(response);
    }


    private Facility mockFacility() {
        Facility facility = new Facility();
        Feature feature = new Feature();
        facility.setFeatureType("S");
        facility.setFeature(feature);
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
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(depositAccount);
        accountSaving.setDepositAccountLists(depositAccountList);
        tmbResponse.setData(accountSaving);
        return ResponseEntity.ok().body(tmbResponse);
    }

}
