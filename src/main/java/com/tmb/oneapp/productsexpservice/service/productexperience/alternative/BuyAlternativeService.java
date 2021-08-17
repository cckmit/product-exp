package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyAlternativeService {

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    private final AlternativeService alternativeService;

    private final CustomerService customerService;

    private final ProductsExpService productsExpService;

    @Autowired
    public BuyAlternativeService(AlternativeService alternativeService,
                                 CustomerService customerService,
                                 ProductsExpService productsExpService) {
        this.alternativeService = alternativeService;
        this.customerService = customerService;
        this.productsExpService = productsExpService;
    }

    public TmbOneServiceResponse<String> validationBuy(String correlationId, String crmId, AlternativeBuyRequest alternativeBuyRequest) {

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);

            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);

            String trackingId = ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING;
            productsExpService.logActivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                    crmId,
                    ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                    trackingId,
                    alternativeBuyRequest));

            // process flag != Y = Can'y By fund
            if(!ProductsExpServiceConstant.PROCESS_FLAG_Y.equals(alternativeBuyRequest.getProcessFlag())){
                status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getCode());
                status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getDesc());
                status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return tmbOneServiceResponse;
            }

            // validate service hour
            tmbOneServiceResponse.setStatus(alternativeService.validateServiceHour(correlationId, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
                return tmbOneServiceResponse;
            }

            // validate age should > 20
            tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
                return tmbOneServiceResponse;
            }

            // validate customer risk level
            tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId,customerInfo, status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
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
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
                return tmbOneServiceResponse;
            }

            // validate flatca flag not valid
            tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid( customerInfo.getFatcaFlag(), status));
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                tmbOneServiceResponse.getStatus().setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
                return tmbOneServiceResponse;
            }

            return tmbOneServiceResponse;

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

}
