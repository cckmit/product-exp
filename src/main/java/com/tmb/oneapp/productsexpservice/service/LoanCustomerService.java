package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanCustomerService {

    public LoanCustomerResponse getCustomerProfile() {
        LoanCustomerResponse response = mockResponse();
        return response;
    }

    public LoanCustomerSubmissionResponse saveCustomerSubmission(LoanCustomerSubmissionRequest request) {
        LoanCustomerSubmissionResponse response = mockCustomerResponse(request);
        return response;
    }


    private LoanCustomerResponse mockResponse() {
        LoanCustomerResponse response = new LoanCustomerResponse();

        List<LoanCustomerFeature> features = new ArrayList<>();

        LoanCustomerFeature feature = new LoanCustomerFeature();
        feature.setId(1L);
        feature.setFeatureType("SB");
        feature.setAmountMin(new BigDecimal(200000));
        feature.setAmountMax(new BigDecimal(200000));
        features.add(feature);

        LoanCustomerFeature feature2 = new LoanCustomerFeature();
        feature2.setId(2L);
        feature2.setFeatureType("CT");
        feature2.setAmountMin(new BigDecimal(300000));
        feature2.setAmountMax(new BigDecimal(500000));
        features.add(feature2);


        LoanCustomerInstallment installment = new LoanCustomerInstallment();
        installment.setId(1L);
        installment.setInstallment("60");

        Pricing pricing = new Pricing();
        pricing.setMonthFrom("1");
        pricing.setMonthTo("3");
        pricing.setRateVaraince(0.25);

        Pricing pricing2 = new Pricing();
        pricing2.setMonthFrom("4");
        pricing2.setMonthTo("5");
        pricing2.setRateVaraince(0.12);

        Pricing pricing3 = new Pricing();
        pricing3.setMonthFrom("4");
        pricing3.setMonthTo("5");
        pricing3.setRateVaraince(0.12);

        AnnualInterest annualInterest = new AnnualInterest();
        annualInterest.setVat(0.07);
        annualInterest.setCharge(0.1);
        annualInterest.setInterest(0.16);

        List<Pricing> pricings = new ArrayList<>();
        pricings.add(pricing);
        pricings.add(pricing2);
        pricings.add(pricing3);

        LoanCustomerInstallment installment2 = new LoanCustomerInstallment();
        installment2.setId(2L);
        installment2.setInstallment("30");

        List<LoanCustomerInstallment> installments= new ArrayList<>();
        installments.add(installment);
        installments.add(installment2);


        List<LoanCustomerDisburstAccount> disburstAccounts = new ArrayList<>();

        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo("123-4-56789-2");
        disburstAccount.setAccountName("บัญชี ออลล์ ฟรี");
        disburstAccount.setBankName("ทีทีบี");
        disburstAccounts.add(disburstAccount);

        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount2.setAccountNo("123-4-56789-0");
        disburstAccount2.setAccountName("บัญชี โนฟิกช์");
        disburstAccount2.setBankName("ทีทีบี");
        disburstAccounts.add(disburstAccount2);

        response.setInstallments(installments);
        response.setFeatures(features);
        response.setDisburstAccounts(disburstAccounts);
        response.setPricings(pricings);
        response.setAnnualInterest(annualInterest);


        return response;
    }

    private LoanCustomerSubmissionResponse mockCustomerResponse(LoanCustomerSubmissionRequest loanCustomerSubmissionRequest) {
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        response.setLimitAmount(new BigDecimal(300000));
        response.setRequestAmount(loanCustomerSubmissionRequest.getRequestAmount());
        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount2.setAccountNo("123-4-56789-0");
        disburstAccount2.setAccountName("บัญชี โนฟิกช์");
        disburstAccount2.setBankName("ทีทีบี");
        response.setDisburstAccount(disburstAccount2);
        response.setInstallment("60");
        return response;
    }


}
