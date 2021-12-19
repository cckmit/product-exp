package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomerService class will get customer data
 */
@Service
public class CustomerService {

    private static final TMBLogger<CustomerService> logger = new TMBLogger<>(CustomerService.class);

    private final CustomerExpServiceClient customerExpServiceClient;

    private final CustomerServiceClient customerServiceClient;

    @Autowired
    public CustomerService(CustomerExpServiceClient customerExpServiceClient, CustomerServiceClient customerServiceClient) {
        this.customerExpServiceClient = customerExpServiceClient;
        this.customerServiceClient = customerServiceClient;
    }

    /**
     * Method to get port fund from MF
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return String
     */
    @LogAround
    public String getAccountSaving(String correlationId, String crmId) {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "accounts/saving", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.fullCrmIdFormat(crmId));
            String response = customerExpServiceClient.getAccountSaving(correlationId, UtilMap.fullCrmIdFormat(crmId));
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "accounts/saving", ProductsExpServiceConstant.LOGGING_RESPONSE), response);
            return response;
        } catch (Exception ex) {
            logger.error("customerExpServiceClient getAccountSaving error: {}", ex);
        }
        return "";
    }

    /**
     * Method to get customer information
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return CustomerSearchResponse
     */
    @LogAround
    public CustomerSearchResponse getCustomerInfo(String correlationId, String crmId) {
        try {
            CrmSearchBody request = CrmSearchBody.builder()
                    .searchType(ProductsExpServiceConstant.SEARCH_TYPE)
                    .searchValue(UtilMap.fullCrmIdFormat(crmId))
                    .build();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "ecprofile", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(request));
            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response =
                    customerServiceClient.customerSearch(correlationId, crmId, request);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "ecprofile", ProductsExpServiceConstant.LOGGING_RESPONSE), response.getBody());
            return response.getBody().getData().get(0);
        } catch (Exception ex) {
            logger.error("error fetch customer search : {}", ex);
        }
        return null;
    }
}
