package com.tmb.oneapp.productsexpservice.mapper.customer;

import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerInfoMapper {
    public CustomerInfo map(CustomerSearchResponse customerResponse) {
        return CustomerInfo.builder()
                .crmId(customerResponse.getCrmId())
                .wealthCrmId(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
                .phoneNumber(customerResponse.getMobileNumber())
                .dateOfBirth(customerResponse.getBirthDate())
                .emailAddress(customerResponse.getEmail())
                .maritalStatus(customerResponse.getMaritalStatus())
                .residentGeoCode("TH")
                .taxNumber(customerResponse.getIdNumber())
                .branchCode(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
                .makerCode(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
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


