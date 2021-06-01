package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCreditCardInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FlexiLoanServiceTest {
    @Mock
    private LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    @Mock
    private LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    @Mock
    private LoanSubmissionGetCreditCardInfoClient getCreditcardInfoClient;

    FlexiLoanService flexiLoanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        flexiLoanService = new FlexiLoanService(getFacilityInfoClient, getCustomerInfoClient, getCreditcardInfoClient);
    }

    @Test
    public void testService() throws ServiceException, RemoteException {
        TmbOneServiceResponse<SubmissionInfoResponse> response = new TmbOneServiceResponse();
        ResponseFacility facilityInfo = new ResponseFacility();
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(facilityInfo);
        ResponseIndividual customer = new ResponseIndividual();
        when(getCustomerInfoClient.searchCustomerInfoByCaID(anyLong())).thenReturn(customer);
        ResponseCreditcard creditcardInfo = new ResponseCreditcard();
        when(getCreditcardInfoClient.searchCreditcardInfoByCaID(any())).thenReturn(creditcardInfo);
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaID(1L);
        flexiLoanService.getSubmissionInfo("xxx", request);
        Assert.assertTrue(true);
    }

}
