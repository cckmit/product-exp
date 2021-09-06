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
 * CustomerService class will get Customer Data
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
     * Method to getPortFolioMutualFund
     *
     * @param correlationId
     * @param crmId
     * @return String
     */
    public String getAccountSaving(String correlationId, String crmId){
        try {
            return customerExpServiceClient.getAccountSaving(correlationId, UtilMap.halfCrmIdFormat(crmId));
        }catch (Exception ex){
            logger.error("customerExpServiceClient getAccountSaving error: {}",ex);
        }
        return "";
    }

    /**
     * Method to getCustomerInfo
     *
     * @param correlationId
     * @param crmId
     * @return CustomerSearchResponse
     */
    @LogAround
    public CustomerSearchResponse getCustomerInfo(String correlationId, String crmId) {
        try {
            CrmSearchBody request = CrmSearchBody.builder()
                    .searchType(ProductsExpServiceConstant.SEARCH_TYPE)
                    .searchValue(UtilMap.halfCrmIdFormat(crmId))
                    .build();
            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response =
                    customerServiceClient.customerSearch(correlationId, crmId, request);
            return response.getBody().getData().get(0);
        }catch (Exception ex){
            logger.error("error fetch customerSearch : {}",ex);
        }
        return null;
    }
}
