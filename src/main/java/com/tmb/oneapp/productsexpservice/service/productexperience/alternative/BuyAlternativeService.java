package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.buy.service.BuyActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice.BuyAndDcaAbstractService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The buy alternative service.
 */
@Service
public class BuyAlternativeService extends BuyAndDcaAbstractService {

    private final BuyActivityLogService buyActivityLogService;

    private static final TMBLogger<BuyAlternativeService> logger = new TMBLogger<>(BuyAlternativeService.class);

    @Autowired
    public BuyAlternativeService(AlternativeService alternativeService,
                                 CustomerService customerService,
                                 ProductsExpService productsExpService,
                                 InvestmentRequestClient investmentRequestClient,
                                 BuyActivityLogService buyActivityLogService) {

        super(alternativeService, customerService, productsExpService, investmentRequestClient);
        this.buyActivityLogService = buyActivityLogService;
    }

    /**
     * Generic Method to validate buy flow, and save activity log, then return response data
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param ipAddress             the ip address
     * @param alternativeBuyRequest the alternative buy request
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    public TmbOneServiceResponse<String> validationBuy(String correlationId, String crmId, String ipAddress, AlternativeBuyRequest alternativeBuyRequest) {
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            String processFlag = alternativeBuyRequest.getProcessFlag();
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId, crmId);

            tmbOneServiceResponse = validateProcessFlag(processFlag, tmbOneServiceResponse, status);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return returnResponseAfterSavingActivityLog(correlationId, crmId, ipAddress, alternativeBuyRequest, tmbOneServiceResponse);
            }

            tmbOneServiceResponse = validateBuyAndDca(correlationId, crmId, customerInfo, tmbOneServiceResponse, status, true, false);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return returnResponseAfterSavingActivityLog(correlationId, crmId, ipAddress, alternativeBuyRequest, tmbOneServiceResponse);
            }

            // validate fund off shelf
            TmbOneServiceResponse<String> fundOffShelf = handleFundOffShelf(correlationId, crmId, alternativeBuyRequest, tmbOneServiceResponse, status);
            if (fundOffShelf != null) return fundOffShelf;

            // validate suitability expired
            tmbOneServiceResponse = validateSuitabilityExpired(correlationId, crmId, tmbOneServiceResponse, status);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return returnResponseAfterSavingActivityLog(correlationId, crmId, ipAddress, alternativeBuyRequest, tmbOneServiceResponse);
            }

            return returnResponseAfterSavingActivityLog(correlationId, crmId, ipAddress, alternativeBuyRequest, tmbOneServiceResponse);

        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    /**
     * Generic Method to handle case fund off shelf
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param alternativeBuyRequest the alternative buy request
     * @param tmbOneServiceResponse the TMB response
     * @param status                the TMB status
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    private TmbOneServiceResponse<String> handleFundOffShelf(String correlationId, String crmId,
                                                             AlternativeBuyRequest alternativeBuyRequest,
                                                             TmbOneServiceResponse<String> tmbOneServiceResponse,
                                                             TmbStatus status) {

        if (StringUtils.isEmpty(alternativeBuyRequest.getFundHouseCode()) ||
                StringUtils.isEmpty(alternativeBuyRequest.getFundCode()) ||
                StringUtils.isEmpty(alternativeBuyRequest.getTranType())) {
            return null;
        }

        tmbOneServiceResponse.setStatus(alternativeService.validateFundOffShelf(
                correlationId, crmId, FundRuleRequestBody.builder()
                        .fundHouseCode(alternativeBuyRequest.getFundHouseCode())
                        .fundCode(alternativeBuyRequest.getFundCode())
                        .tranType(alternativeBuyRequest.getTranType())
                        .build(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            String reason = tmbOneServiceResponse.getStatus().getDescription();
            return returnResponseAfterSavingActivityLog(correlationId, crmId, reason, alternativeBuyRequest, tmbOneServiceResponse);
        }

        return null;
    }

    /**
     * Generic Method to save activity log, then return the values back
     *
     * @param correlationId         the correlation id
     * @param crmId                 the crm id
     * @param ipAddress             the ip address
     * @param alternativeBuyRequest the alternative buy request
     * @param tmbOneServiceResponse the TMB response
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    private TmbOneServiceResponse<String> returnResponseAfterSavingActivityLog(String correlationId, String crmId, String ipAddress,
                                                                               AlternativeBuyRequest alternativeBuyRequest,
                                                                               TmbOneServiceResponse<String> tmbOneServiceResponse) {

        buyActivityLogService.clickPurchaseButtonAtFundFactSheetScreen(correlationId, crmId, ipAddress, alternativeBuyRequest, tmbOneServiceResponse);
        return tmbOneServiceResponse;
    }
}
