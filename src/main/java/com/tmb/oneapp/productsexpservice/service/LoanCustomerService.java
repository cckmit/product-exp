package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerFeature;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerInstallment;
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
        feature.setAmount(new BigDecimal(200000));
        features.add(feature);

        LoanCustomerFeature feature2 = new LoanCustomerFeature();
        feature2.setId(2L);
        feature2.setFeatureType("CT");
        feature2.setAmount(new BigDecimal(300000));
        features.add(feature2);

        LoanCustomerInstallment installment = new LoanCustomerInstallment();
        installment.setId(1L);
        installment.setInstallment("60");

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
        disburstAccounts.add(disburstAccount);

        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount2.setAccountNo("123-4-56789-0");
        disburstAccount2.setAccountName("บัญชี โนฟิกช์");
        disburstAccounts.add(disburstAccount2);

        response.setInstallments(installments);
        response.setFeatures(features);
        response.setDisburstAccounts(disburstAccounts);


        return response;
    }


}
