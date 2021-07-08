package com.tmb.oneapp.productsexpservice.mapper.customer;

import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.CustomerInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
public class CustomerInfoMapper {
    private static final TMBLogger<CustomerInfoMapper> logger = new TMBLogger<>(CustomerInfoMapper.class);

    private String customerServiceDateFormat = "yyyy-MM-dd";
    private String openPortFolioDateFormat = "yyyy-MM-dd'T'HH:mm:ss";

    public CustomerInfo map(CustomerSearchResponse customerResponse) {
        try{
            SimpleDateFormat openPortFormat = new SimpleDateFormat(openPortFolioDateFormat);
            return CustomerInfo.builder()
                    .crmId(customerResponse.getCrmId())
                    .wealthCrmId(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
                    .phoneNumber(customerResponse.getMobileNumber())
                    .dateOfBirth(formatDateForOpenPortFolio(customerResponse.getBirthDate()))
                    .emailAddress(customerResponse.getEmail())
                    .maritalStatus(customerResponse.getMaritalStatus())
                    .residentGeoCode("TH")
                    .taxNumber(customerResponse.getIdNumber())
                    .branchCode(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
                    .makerCode(ProductsExpServiceConstant.MIB_CUSTOMER_STATIC_ID)
                    .kycFlag(customerResponse.getEkycFlag())
                    .amloFlag(customerResponse.getAmloFlag())
                    .lastDateSync(openPortFormat.format(Calendar.getInstance().getTime()))
                    .nationalDocumentExpireDate(formatDateForOpenPortFolio(customerResponse.getExpiryDate()))
                    .nationalDocumentId(customerResponse.getIdNumber())
                    .nationalDocumentIdentificationType("TMB_CITIZEN_ID")
                    .customerFirstNameEn(customerResponse.getCustomerEnglishFirstName())
                    .customerFirstNameTh(customerResponse.getCustomerThaiFirstName())
                    .customerLastNameEn(customerResponse.getCustomerEnglishLastName())
                    .customerLastNameTh(customerResponse.getCustomerThaiLastName())
                    .customerRiskLevel(customerResponse.getCustomerRiskLevel())
                    .registerAddress(customerResponse.getRegisterAddress())
                    .contactAddress(customerResponse.getContactAddress())
                    .officeAddress(customerResponse.getOfficeAddress())
                    .build();
        }catch (ParseException ex){
            logger.info("Error ParseException");
        }
        return null;
    }

    private String formatDateForOpenPortFolio(String customerDateString) throws ParseException {
        SimpleDateFormat customerFormat = new SimpleDateFormat(customerServiceDateFormat);
        SimpleDateFormat openPortFormat = new SimpleDateFormat(openPortFolioDateFormat);
        return !StringUtils.isEmpty(customerDateString)?
                openPortFormat.format(customerFormat.parse(customerDateString)):"";
     }
}


