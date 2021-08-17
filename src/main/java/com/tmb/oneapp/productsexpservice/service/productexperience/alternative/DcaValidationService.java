package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.enums.DcaValidationErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DcaValidationService class will validate for dca fund and get fund fact sheet
 */
@Service
public class DcaValidationService {

    private static final TMBLogger<DcaValidationService> logger = new TMBLogger<>(DcaValidationService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final CustomerService customerService;

    private final AlternativeService alternativeService;

    @Autowired
    public DcaValidationService(InvestmentRequestClient investmentRequestClient,
                                CustomerService customerService,
                                AlternativeService alternativeService) {
        this.investmentRequestClient = investmentRequestClient;
        this.customerService = customerService;
        this.alternativeService = alternativeService;
    }

    /**
     * Method dcaValidation to call MF Service account saving and fund rule and fund fact sheet
     *
     * @param correlationId
     * @param crmId
     * @param dcaValidationRequest
     * @return TmbOneServiceResponse<DcaInformationDto>
     */
    public TmbOneServiceResponse<DcaValidationDto> dcaValidation(String correlationId, String crmId, DcaValidationRequest dcaValidationRequest) {

        TmbOneServiceResponse<DcaValidationDto> dcaValidationDtoTmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = TmbStatusUtil.successStatus();
        dcaValidationDtoTmbOneServiceResponse.setStatus(tmbStatus);

        try {
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);

            tmbStatus = validatePtesPort(crmId, dcaValidationRequest, invHeaderReqParameter, dcaValidationDtoTmbOneServiceResponse.getStatus());
            if (!tmbStatus.getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return dcaValidationDtoTmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRule = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, FundRuleRequestBody.builder()
                    .fundCode(dcaValidationRequest.getFundCode())
                    .fundHouseCode(dcaValidationRequest.getFundHouseCode())
                    .tranType(dcaValidationRequest.getTranType())
                    .build());
            tmbStatus = validateAllowAipFlag(fundRule, dcaValidationDtoTmbOneServiceResponse.getStatus());
            if (!tmbStatus.getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return dcaValidationDtoTmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> fundFactSheet = investmentRequestClient.callInvestmentFundFactSheetService(invHeaderReqParameter, FundFactSheetRequestBody.builder().fundCode(dcaValidationRequest.getFundCode()).language(dcaValidationRequest.getLanguage()).build());
            dcaValidationDtoTmbOneServiceResponse.setData(DcaValidationDto.builder().factSheetData(fundFactSheet.getBody().getData().getBody().getFactSheetData()).build());
            return dcaValidationDtoTmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error("error : {}", ex);
            dcaValidationDtoTmbOneServiceResponse.setStatus(null);
            dcaValidationDtoTmbOneServiceResponse.setData(null);
            return dcaValidationDtoTmbOneServiceResponse;
        }
    }

    private TmbStatus validateAllowAipFlag(ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRule, TmbStatus tmbStatus) throws TMBCommonException {
        if (!fundRule.getStatusCode().equals(HttpStatus.OK)) {
            throw new TMBCommonException("failed fetch fund rule");
        }
        FundRuleInfoList fundRuleInfoList = fundRule.getBody().getData().getFundRuleInfoList().get(0);
        if (!fundRuleInfoList.getAllowAipFlag().equals(ProductsExpServiceConstant.APPLICATION_STATUS_FLAG_TRUE)) {
            tmbStatus.setCode(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getCode());
            tmbStatus.setMessage(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getMsg());
            tmbStatus.setDescription(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getDesc());
            return tmbStatus;
        }
        return tmbStatus;
    }

    private TmbStatus validatePtesPort(String crmId, DcaValidationRequest dcaValidationRequest, Map<String, String> invHeaderReqParameter, TmbStatus tmbStatus) {

        ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> ptesPort = investmentRequestClient.getPtesPort(invHeaderReqParameter, crmId);
        List<PtesDetail> ptesPortList = ptesPort.getBody().getData();
        Optional<PtesDetail> ptesPortOptional = ptesPortList.stream()
                .filter(t -> t.getPortfolioFlag().equals(ProductsExpServiceConstant.PTES_PORT_FOLIO_FLAG) &&
                        t.getPortfolioNumber().equals(dcaValidationRequest.getPortfolioNumber()))
                .findFirst();
        if (ptesPortOptional.isPresent()) {
            tmbStatus.setCode(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getCode());
            tmbStatus.setMessage(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getMsg());
            tmbStatus.setDescription(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getDesc());
            return tmbStatus;
        }

        return tmbStatus;
    }

    public TmbOneServiceResponse<String> validationAlternativeDca(String correlationId, String crmId,String processFlag) {
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);

            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId,crmId);

            // process flag != Y = Can'y By fund
            if(!ProductsExpServiceConstant.PROCESS_FLAG_Y.equals(processFlag)){
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
