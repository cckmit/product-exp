package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public Map<String, String> createNcbCase(String crmId, String correlationId, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn, String serviceTypeMatrixCode) {
        try {
            String firstname = (!firstnameTh.isEmpty())? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty())? lastnameTh : lastnameEn;

            ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                    customerServiceClient.submitNcbCustomerCase(crmId, correlationId, firstname, lastname, serviceTypeMatrixCode);

            Map<String, String> result = new HashMap<>();
            String caseNumberSnakeCase = "case_number";

            result.put(caseNumberSnakeCase, response.getBody().getData().get("case_number"));  //NOSONAR lightweight logging

            return result; //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("Unexpected error occured : {}", e);
            return new HashMap<>();
        }
    }
}
