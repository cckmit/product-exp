package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerFeature;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
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

    private LoanCustomerResponse mockResponse() {
        LoanCustomerResponse response = new LoanCustomerResponse();

        List<LoanCustomerFeature> features = new ArrayList<>();

        LoanCustomerFeature feature = new LoanCustomerFeature();
        feature.setId(1L);
        feature.setFeatureType("SB");
        feature.setAmount(new BigDecimal("200,000"));
        features.add(feature);

        LoanCustomerFeature feature2 = new LoanCustomerFeature();
        feature.setId(2L);
        feature.setFeatureType("CT");
        feature.setAmount(new BigDecimal("300,000"));
        features.add(feature2);

        LoanCustomerPricing princing = new LoanCustomerPricing();
        princing.setId(1L);
        princing.setRate(0.25);
        princing.setMonthFrom("1");
        princing.setMonthTo("3");
        princing.setTier(1L);

        List<LoanCustomerPricing> pricings = new ArrayList<>();
        pricings.add(princing);


        List<LoanCustomerDisburstAccount> disburstAccounts = new ArrayList<>();

        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo("123-4-56789-2");
        disburstAccount.setAccountName("บัญชี ออลล์ ฟรี");
        disburstAccounts.add(disburstAccount);

        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo("123-4-56789-0");
        disburstAccount.setAccountName("บัญชี โนฟิกช์");
        disburstAccounts.add(disburstAccount2);



        return response;
    }


}
