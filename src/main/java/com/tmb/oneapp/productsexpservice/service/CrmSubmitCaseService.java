package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.crm.CustomerCaseSubmitBody;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    @LogAround
    public Map<String, String> createCrmCase(String crmId, String correlationId, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn, String serviceTypeMatrixCode, String note) { //NOSONAR lightweight logging
        try {
            String firstname = (!firstnameTh.isEmpty())? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty())? lastnameTh : lastnameEn;

            byte[] bytesFirstname = firstname.getBytes(StandardCharsets.UTF_8);
            firstname = new String(bytesFirstname, StandardCharsets.UTF_8);
            byte[] bytesLastname = lastname.getBytes(StandardCharsets.UTF_8);
            lastname = new String(bytesLastname, StandardCharsets.UTF_8);

            CustomerCaseSubmitBody customerCaseSubmitBody = new CustomerCaseSubmitBody(firstname, lastname, serviceTypeMatrixCode, note);

            ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                    customerServiceClient.submitCustomerCase(crmId, correlationId, customerCaseSubmitBody);

            Map<String, String> result = new HashMap<>();
            result.put(ProductsExpServiceConstant.CASE_NUMBER, response.getBody().getData().get(ProductsExpServiceConstant.CASE_NUMBER));  //NOSONAR lightweight logging
            result.put(ProductsExpServiceConstant.TRANSACTION_DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date()));

            return result; //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("createNcbCase error : {}", e);
            return null;
        }
    }
}
