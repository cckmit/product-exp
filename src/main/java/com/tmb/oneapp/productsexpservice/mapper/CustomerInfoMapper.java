package com.tmb.oneapp.productsexpservice.mapper;

import com.tmb.oneapp.productsexpservice.model.openportfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.response.customer.CustomerSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerInfoMapper {
    public CustomerInfo map(CustomerSearchResponse customerResponse) {
        return CustomerInfo.builder()
                .crmId(customerResponse.getRmId())
                .wealthCrmId("D0000000988")
                .phoneNumber(customerResponse.getMobileNumber())
                .dateOfBirth(customerResponse.getBirthDate())
                .emailAddress(customerResponse.getEmail())
                .maritalStatus(customerResponse.getMaritalStatus())
                .residentGeoCode("TH")
                .taxNumber(customerResponse.getIdNo())
                .branchCode("D0000000988")
                .makerCode("D0000000988")
                .kycFlag(customerResponse.getEkycFlag())
                .amloFlag(customerResponse.getAmloFlag())
                .lastDateSync(String.valueOf(System.currentTimeMillis()))
                .nationalDocumentExpireDate(customerResponse.getExpiryDate())
                .nationalDocumentId(customerResponse.getIdNo())
                .nationalDocumentIdentificationType("TMB_CITIZEN_ID")
                .customerFirstNameEn(customerResponse.getCustomerFirstNameEn())
                .customerFirstNameTh(customerResponse.getCustomerFirstNameTh())
                .customerLastNameEn(customerResponse.getCustomerLastNameEn())
                .customerLastNameTh(customerResponse.getCustomerLastNameTh())
                .build();
    }
}


