package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * CrmSubmitCaseService submit crm case
 */
@Service
public class CrmSubmitCaseService {
    private static final TMBLogger<CrmSubmitCaseService> logger = new TMBLogger<>(CrmSubmitCaseService.class);

    private final CustomerServiceClient customerServiceClient;

    public CrmSubmitCaseService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }
    /**
     * confirm payment of NCB
     *
     * @param crmId crmId
     * @param correlationId  correlationId
     * @param firstnameTh  firstnameTh
     * @param lastnameTh  lastnameTh
     * @param firstnameEn  firstnameEn
     * @param lastnameEn  lastnameEn
     * @param serviceTypeMatrixCode serviceTypeMatrixCode
     *
     * @return NcbPaymentConfirmResponse NcbPaymentConfirmResponse
     */
    public Map<String, String> createNcbCase(String crmId, String correlationId, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn, String serviceTypeMatrixCode) {
        try {
            logger.info("product-exp-service createNcbCase method start Time : {} ", System.currentTimeMillis());

            String firstname = (!firstnameTh.isEmpty())? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty())? lastnameTh : lastnameEn;

            byte[] bytesFirstname = firstname.getBytes(StandardCharsets.UTF_8);
            firstname = new String(bytesFirstname, StandardCharsets.UTF_8);
            byte[] bytesLastname = lastname.getBytes(StandardCharsets.UTF_8);
            lastname = new String(bytesLastname, StandardCharsets.UTF_8);

            ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                    customerServiceClient.submitNcbCustomerCase(crmId, correlationId, firstname, lastname, serviceTypeMatrixCode);

            Map<String, String> result = new HashMap<>();
            result.put(ProductsExpServiceConstant.CASE_NUMBER, response.getBody().getData().get(ProductsExpServiceConstant.CASE_NUMBER));  //NOSONAR lightweight logging

            return result; //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("createNcbCase error : {}", e);
            return new HashMap<>();
        }
    }
}
