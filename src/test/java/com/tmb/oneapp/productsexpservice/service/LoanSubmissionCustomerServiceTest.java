package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.LoanOnlineInterestRate;
import com.tmb.common.model.LoanOnlineRangeIncome;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.facility.response.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class LoanSubmissionCustomerServiceTest {

    @Mock
    private LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;

    @Mock
    private CommonServiceClient commonServiceClient;

    LoanSubmissionCustomerService loanSubmissionCustomerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionCustomerService = new LoanSubmissionCustomerService(getFacilityInfoClient, commonServiceClient);
    }

    @Test
    public void testGetCustomerInfoSuccess() throws ServiceException, RemoteException {
        ResponseFacility respFacility = mockFacility1();
        Body body = new Body();
        body.setFacilities(respFacility.getBody().getFacilities());


        com.tmb.common.model.legacy.rsl.ws.facility.response.Header header = new com.tmb.common.model.legacy.rsl.ws.facility.response.Header();
        header.setChannel("MIB");
        header.setModule("3");
        header.setResponseCode("MSG_000");
        header.setRequestID(UUID.randomUUID().toString());
        respFacility.setHeader(header);

        com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility responseFacility = new  com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility();
        com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header header1 = new com.tmb.common.model.legacy.rsl.ws.facility.update.response.Header();
        header1.setResponseCode("MSG_000");
        responseFacility.setHeader(header1);

        TmbOneServiceResponse<List<LoanOnlineInterestRate>> loanOnlineTmbOneServiceResponse = new TmbOneServiceResponse<List<LoanOnlineInterestRate>>();
        List<LoanOnlineInterestRate> interestRateResponse = new ArrayList<>();
        LoanOnlineInterestRate interestRate = new LoanOnlineInterestRate();
        interestRate.setInterestRate(17);
        interestRate.setProductCode("RC01");
        interestRate.setEmploymentStatus("salary");
        interestRate.setRangeIncomeMax(99999);
        interestRate.setRangeIncomeMin(20000);
        interestRate.setEmploymentStatusId("01");
        interestRateResponse.add(interestRate);

        loanOnlineTmbOneServiceResponse.setData(interestRateResponse);
        loanOnlineTmbOneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> oneServiceResponse = new TmbOneServiceResponse<List<LoanOnlineRangeIncome>>();
        List<LoanOnlineRangeIncome> rangeIncomeList = new ArrayList<>();
        LoanOnlineRangeIncome rangeIncome = new LoanOnlineRangeIncome();
        rangeIncome.setRevenueMultiple(5);
        rangeIncome.setProductCode("RC01");
        rangeIncome.setEmploymentStatus("salary");
        rangeIncome.setRangeIncomeMaz(99999);
        rangeIncome.setRangeIncomeMin(20000);
        rangeIncome.setEmploymentStatusId("01");
        rangeIncomeList.add(rangeIncome);

        oneServiceResponse.setData(rangeIncomeList);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

        when(getFacilityInfoClient.searchFacilityInfoByCaID(any())).thenReturn(respFacility);
        when(commonServiceClient.getInterestRateAll()).thenReturn(ResponseEntity.ok(loanOnlineTmbOneServiceResponse));
        when(commonServiceClient.getRangeIncomeAll()).thenReturn(ResponseEntity.ok(oneServiceResponse));

        LoanSubmissionResponse loanSubmissionResponse = loanSubmissionCustomerService.getCustomerInfo(2021062304188182L);
        Assert.assertNotNull(loanSubmissionResponse);

    }



    private ResponseFacility mockFacility1() {
        ResponseFacility responseFacility = new ResponseFacility();
        Facility f = new Facility();
        Feature feature = new Feature();
        com.tmb.common.model.legacy.rsl.ws.facility.response.Body body = new com.tmb.common.model.legacy.rsl.ws.facility.response.Body();
        f.setCardDelivery("H");
        f.setCaId(BigDecimal.TEN);
        f.setFeatureType("S");
        f.setCaCampaignCode("U");
        f.setAmountFinance(BigDecimal.TEN);
        f.setDisburstBankName("ttb");
        f.setDisburstAccountName("ttb");
        f.setDisburstAccountNo("111");
        f.setOutStandingBalance(BigDecimal.TEN);
        f.setConsiderLoanWithOtherBank("bkk");
        f.setCreditLimitFromMof(BigDecimal.TEN);
        f.setExistingAccountNo("111");
        f.setExistingCreditLimit(BigDecimal.TEN);
        f.setExistLoan("aaa");
        f.setPricings(mockPricing());
        feature.setRequestAmount(BigDecimal.TEN);
        f.setFeature(feature);

        Facility[] facilitys = new Facility[1];
        facilitys[0] = f;
        body.setFacilities(facilitys);
        responseFacility.setBody(body);
        return responseFacility;
    }

    private Pricing[] mockPricing() {
        InstantLoanCalUWResponse instantLoanCalUWResponse = new InstantLoanCalUWResponse();
        Pricing[] pricings = new Pricing[1];
        Pricing p = new Pricing();
        p.setMonthFrom(BigDecimal.ONE);
        p.setMonthTo(BigDecimal.ONE);
        p.setPercentSign("S");
        p.setPricingType("");
        p.setRateType("S");
        p.setRateVaraince(BigDecimal.ONE);
        pricings[0] = p;

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        pricings[0].setMonthTo(BigDecimal.ONE);
        pricings[0].setMonthFrom(BigDecimal.ONE);
        pricings[0].setRateVaraince(BigDecimal.ONE);

        LoanCustomerPricing pricing = new LoanCustomerPricing();
        pricing.setMonthTo(pricings[0].getMonthTo());
        pricing.setMonthFrom(pricings[0].getMonthFrom());
        pricing.setRateVariance(pricings[0].getRateVaraince());
        pricing.setRate("1");
        pricingList.add(pricing);
        instantLoanCalUWResponse.setPricings(pricingList);
        return pricings;
    }
}