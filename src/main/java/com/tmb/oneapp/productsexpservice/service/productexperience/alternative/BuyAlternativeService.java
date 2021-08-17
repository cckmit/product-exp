package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.stereotype.Service;

@Service
public class BuyAlternativeService {

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    private final AlternativeService alternativeService;

    private final CustomerService customerService;

    private final ProductsExpService productsExpService;

    public BuyAlternativeService(AlternativeService alternativeService,
                                 CustomerService customerService,
                                 ProductsExpService productsExpService) {
        this.alternativeService = alternativeService;
        this.customerService = customerService;
        this.productsExpService = productsExpService;
    }

    public TmbOneServiceResponse<String> validationBuy(String correlationId, String crmId, AlternativeRequest alternativeRequest) {

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);

            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);

            // validate service hour
            tmbOneServiceResponse.setStatus(alternativeService.validateServiceHour(correlationId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate age should > 20
            tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate customer risk level
            tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId,customerInfo, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate customer assurange level
            tmbOneServiceResponse.setStatus(alternativeService.validateIdentityAssuranceLevel(customerInfo.getEkycIdentifyAssuranceLevel(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate casa dormant
            tmbOneServiceResponse.setStatus(alternativeService.validateCASADormant(correlationId, crmId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate suitability expired
            tmbOneServiceResponse.setStatus(alternativeService.validateSuitabilityExpired(correlationId, crmId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate id card expired
            tmbOneServiceResponse.setStatus(alternativeService.validateIdCardExpired( crmId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            // validate flatca flag not valid
            tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid( customerInfo.getFatcaFlag(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            String trackingId = ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING;
            productsExpService.logActivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                    crmId,
                    ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                    trackingId,
                    alternativeRequest));

            return tmbOneServiceResponse;

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

}
