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
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionPricingInfo;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
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
import java.util.ArrayList;
import java.util.List;

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
    public void testService_creditCard() throws ServiceException, RemoteException {
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(mockFacilityInfo());
        when(getCustomerInfoClient.searchCustomerInfoByCaID(anyLong())).thenReturn(mockCustomerInfo());
        when(getCreditCardInfoClient.searchCreditcardInfoByCaID(any())).thenReturn(mockCreditCardInfo());
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("VI");
        flexiLoanService.getSubmissionInfo(request);
        Assert.assertTrue(true);
    }

    @Test
    public void testService_c2g() throws ServiceException, RemoteException {
        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(mockFacilityInfoWithRateType());
        when(getCustomerInfoClient.searchCustomerInfoByCaID(anyLong())).thenReturn(mockCustomerInfo());
        when(getCreditCardInfoClient.searchCreditcardInfoByCaID(any())).thenReturn(mockCreditCardInfo());
        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("C2G");
        flexiLoanService.getSubmissionInfo(request);
        Assert.assertTrue(true);
    }

    private ResponseFacility mockFacilityInfo() {
        ResponseFacility facilityInfo = new ResponseFacility();
        SubmissionPricingInfo submissionPricingInfo = new SubmissionPricingInfo();

        Body body = new Body();
        Facility facility = new Facility();

        Pricing p = new Pricing();
        p.setRateVaraince(BigDecimal.TEN);
        Pricing[] pricings = new Pricing[1];
        pricings[0] = p;

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        pricings[0].setMonthTo(BigDecimal.ONE);
        pricings[0].setMonthFrom(BigDecimal.ONE);
        pricings[0].setRateVaraince(BigDecimal.ONE);
        pricings[0].setYearFrom(BigDecimal.ONE);
        pricings[0].setYearTo(BigDecimal.ONE);
        pricings[0].setPricingType("S");

        LoanCustomerPricing customerPricing = new LoanCustomerPricing();
        customerPricing.setYearFrom(BigDecimal.ONE);
        customerPricing.setYearTo(BigDecimal.ONE);
        customerPricing.setMonthFrom(BigDecimal.ONE);
        customerPricing.setMonthTo(BigDecimal.ONE);
        customerPricing.setRate("12");
        customerPricing.setRateVariance(BigDecimal.ONE);
        pricingList.add(customerPricing);
        submissionPricingInfo.setPricing(pricingList);

        facility.setPricings(pricings);

        Feature feature = new Feature();
        feature.setDisbAcctNo("xxx");
        facility.setFeature(feature);

        facility.setFeatureType("S");

        Facility[] facilities = {facility};
        body.setFacilities(facilities);
        facilityInfo.setBody(body);
        return facilityInfo;
    }

    private ResponseFacility mockFacilityInfoWithRateType() {
        ResponseFacility facilityInfo = new ResponseFacility();
        SubmissionPricingInfo submissionPricingInfo = new SubmissionPricingInfo();

        Pricing p = new Pricing();
        Body body = new Body();
        Facility facility = new Facility();
        Pricing[] pricings = new Pricing[1];
        pricings[0] = p;

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        pricings[0].setMonthTo(BigDecimal.ONE);
        pricings[0].setMonthFrom(BigDecimal.ONE);
        pricings[0].setRateVaraince(BigDecimal.ONE);
        pricings[0].setYearFrom(BigDecimal.ONE);
        pricings[0].setYearTo(BigDecimal.ONE);
        pricings[0].setPricingType("S");

        LoanCustomerPricing customerPricing = new LoanCustomerPricing();
        customerPricing.setYearFrom(BigDecimal.ONE);
        customerPricing.setYearTo(BigDecimal.ONE);
        customerPricing.setMonthFrom(BigDecimal.ONE);
        customerPricing.setMonthTo(BigDecimal.ONE);
        customerPricing.setRate("12");
        customerPricing.setRateVariance(BigDecimal.ONE);
        pricingList.add(customerPricing);
        submissionPricingInfo.setPricing(pricingList);

        Pricing pricing = new Pricing();
        pricing.setRateVaraince(BigDecimal.TEN);
        pricing.setRateType("CPR");
        pricing.setPercentSign("+");

        facility.setPricings(pricings);

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
