package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.common.ob.creditcard.CreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.common.ob.individual.Individual;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCreditCardInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
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
    private LoanSubmissionGetCreditCardInfoClient getCreditCardInfoClient;

    FlexiLoanService flexiLoanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        flexiLoanService = new FlexiLoanService(getFacilityInfoClient, getCustomerInfoClient, getCreditCardInfoClient);
    }

    @Test
    public void testService() throws ServiceException, RemoteException {
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(mockFacilityInfo());
        when(getCustomerInfoClient.searchCustomerInfoByCaID(anyLong())).thenReturn(mockCustomerInfo());
        when(getCreditCardInfoClient.searchCreditcardInfoByCaID(any())).thenReturn(mockCreditCardInfo());
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaID(1L);
        flexiLoanService.getSubmissionInfo(request);
        Assert.assertTrue(true);
    }

    private ResponseFacility mockFacilityInfo() {
        ResponseFacility facilityInfo = new ResponseFacility();
        Body body = new Body();
        Facility facility = new Facility();

        Pricing pricing = new Pricing();
        pricing.setRateVaraince(BigDecimal.TEN);
        Pricing[] pricingList = {pricing};
        facility.setPricings(pricingList);

        Feature feature = new Feature();
        feature.setDisbAcctNo("xxx");
        facility.setFeature(feature);

        facility.setFeatureType("S");

        Facility[] facilities = {facility};
        body.setFacilities(facilities);
        facilityInfo.setBody(body);
        return facilityInfo;
    }

    private ResponseIndividual mockCustomerInfo() {
        ResponseIndividual customerInfo = new ResponseIndividual();
        com.tmb.common.model.legacy.rsl.ws.individual.response.Body body = new com.tmb.common.model.legacy.rsl.ws.individual.response.Body();
        Individual individual = new Individual();
        Individual[] individuals = {individual};
        body.setIndividuals(individuals);
        customerInfo.setBody(body);
        return customerInfo;
    }

    private ResponseCreditcard mockCreditCardInfo() {
        ResponseCreditcard creditCardInfo = new ResponseCreditcard();
        com.tmb.common.model.legacy.rsl.ws.creditcard.response.Body body = new com.tmb.common.model.legacy.rsl.ws.creditcard.response.Body();
        CreditCard creditCard = new CreditCard();
        CreditCard[] creditCards = {creditCard};
        body.setCreditCards(creditCards);
        creditCardInfo.setBody(body);
        return creditCardInfo;
    }

}
