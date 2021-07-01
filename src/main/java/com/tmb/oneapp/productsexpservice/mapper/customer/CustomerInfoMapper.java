package com.tmb.oneapp.productsexpservice.mapper.customer;

import com.tmb.oneapp.productsexpservice.model.portfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.response.customer.CustomerSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerInfoMapper {
    public CustomerInfo map(CustomerSearchResponse customerResponse) {
        return CustomerInfo.builder()
                .crmId(customerResponse.getCrmId())
                .wealthCrmId("D0000000988")
                .phoneNumber(customerResponse.getMobileNumber())
                .dateOfBirth(customerResponse.getBirthDate())
                .emailAddress(customerResponse.getEmail())
                .maritalStatus(customerResponse.getMaritalStatus())
                .residentGeoCode("TH")
                .taxNumber(customerResponse.getIdNumber())
                .branchCode("D0000000988")
                .makerCode("D0000000988")
                .kycFlag(customerResponse.getEkycFlag())
                .amloFlag(customerResponse.getAmloFlag())
                .lastDateSync(String.valueOf(System.currentTimeMillis()))
                .nationalDocumentExpireDate(customerResponse.getExpiryDate())
                .nationalDocumentId(customerResponse.getIdNumber())
                .nationalDocumentIdentificationType("TMB_CITIZEN_ID")
                .customerFirstNameEn(customerResponse.getCustomerEnglishFirstName())
                .customerFirstNameTh(customerResponse.getCustomerThaiFirstName())
                .customerLastNameEn(customerResponse.getCustomerEnglishLastName())
                .customerLastNameTh(customerResponse.getCustomerThaiLastName())
                .build();
    }
}


