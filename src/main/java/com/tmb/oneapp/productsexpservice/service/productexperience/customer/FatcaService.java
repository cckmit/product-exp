package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.fatca.service.FatcaActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request.FatcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * FatcaService class will handle all of logic about fatca
 */
@Service
public class FatcaService {

    private static final TMBLogger<FatcaService> logger = new TMBLogger<>(FatcaService.class);

    private final CustomerService customerService;

    private final CustomerExpServiceClient customerExpServiceClient;

    private final FatcaActivityLogService fatcaActivityLogService;

    @Autowired
    public FatcaService(CustomerService customerService, CustomerExpServiceClient customerExpServiceClient, FatcaActivityLogService fatcaActivityLogService) {
        this.customerService = customerService;
        this.customerExpServiceClient = customerExpServiceClient;
        this.fatcaActivityLogService = fatcaActivityLogService;
    }

    /**
     * Method to get port fund from MF
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param ipAddress     the ip address
     * @param fatcaRequest  the fatca request
     * @return String
     */
    @LogAround
    public TmbOneServiceResponse<FatcaResponseBody> createFatcaForm(String correlationId, String crmId, String ipAddress, FatcaRequest fatcaRequest) {
        TmbOneServiceResponse<FatcaResponseBody> response = new TmbOneServiceResponse<>();

        try {
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId, crmId);

            if (!StringUtils.isEmpty(customerInfo)) {
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "fatca/creation", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.fullCrmIdFormat(crmId));
                ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> fatcaForm = customerExpServiceClient.createFatcaForm(correlationId, UtilMap.fullCrmIdFormat(crmId), fatcaRequest);
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "fatca/creation", ProductsExpServiceConstant.LOGGING_RESPONSE), fatcaForm);

                fatcaActivityLogService.clickNextButtonAtFatcaQuestionScreen(correlationId, crmId, ipAddress, customerInfo.getFatcaFlag(), fatcaForm.getBody());
                return fatcaForm.getBody();
            } else {
                TmbStatus status = TmbStatusUtil.badRequestStatus();
                status.setDescription("error fetch customer search");
                response.setStatus(status);
            }
        } catch (Exception ex) {
            logger.error("customer-exp service client at get account saving error: {}", ex);
            response.setStatus(TmbStatusUtil.badRequestStatus());
        }
        fatcaActivityLogService.clickNextButtonAtFatcaQuestionScreen(correlationId, crmId, ipAddress, "", response);
        return response;
    }
}
