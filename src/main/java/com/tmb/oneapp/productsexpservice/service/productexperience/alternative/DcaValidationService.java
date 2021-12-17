package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.DcaValidationErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetData;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.abstractservice.BuyAndDcaAbstractService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
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
public class DcaValidationService extends BuyAndDcaAbstractService {

    private static final TMBLogger<DcaValidationService> logger = new TMBLogger<>(DcaValidationService.class);

    public DcaValidationService(AlternativeService alternativeService, CustomerService customerService, ProductsExpService productsExpService, InvestmentRequestClient investmentRequestClient) {
        super(alternativeService, customerService, productsExpService, investmentRequestClient);
    }

    /**
     * Generic Method to call MF Service account saving and fund rule and fund fact sheet
     *
     * @param correlationId        the correlation id
     * @param crmId                the crm id
     * @param dcaValidationRequest the dca validation request
     * @return TmbOneServiceResponse<DcaInformationDto>
     */
    @LogAround
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

            FundRuleRequestBody fundRuleRequestBody = FundRuleRequestBody.builder()
                    .fundCode(dcaValidationRequest.getFundCode())
                    .fundHouseCode(dcaValidationRequest.getFundHouseCode())
                    .tranType(dcaValidationRequest.getTranType())
                    .build();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fundRule", ProductsExpServiceConstant.LOGGING_REQUEST), fundRuleRequestBody);
            ResponseEntity<TmbOneServiceResponse<FundRuleResponse>> fundRule = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fundRule", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(fundRule.getBody()));

            tmbStatus = validateAllowAipFlag(fundRule, dcaValidationDtoTmbOneServiceResponse.getStatus());
            if (!tmbStatus.getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return dcaValidationDtoTmbOneServiceResponse;
            }

            FundFactSheetRequestBody fundFactSheetRequestBody = FundFactSheetRequestBody.builder().fundCode(dcaValidationRequest.getFundCode()).language(dcaValidationRequest.getLanguage()).build();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fundFactSheet", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(fundFactSheetRequestBody));
            ResponseEntity<TmbOneServiceResponse<FundFactSheetData>> fundFactSheet = investmentRequestClient.callInvestmentFundFactSheetService(invHeaderReqParameter, FundFactSheetRequestBody.builder().fundCode(dcaValidationRequest.getFundCode()).language(dcaValidationRequest.getLanguage()).build());
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fundFactSheet", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(fundFactSheet.getBody().getStatus()));

            dcaValidationDtoTmbOneServiceResponse.setData(DcaValidationDto.builder().factSheetData(fundFactSheet.getBody().getData().getFactSheetData()).build());
            return dcaValidationDtoTmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error("error : {}", ex);
            dcaValidationDtoTmbOneServiceResponse.setStatus(null);
            dcaValidationDtoTmbOneServiceResponse.setData(null);
            return dcaValidationDtoTmbOneServiceResponse;
        }
    }

    @LogAround
    private TmbStatus validateAllowAipFlag(ResponseEntity<TmbOneServiceResponse<FundRuleResponse>> fundRule, TmbStatus tmbStatus) throws TMBCommonException {
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

    @LogAround
    private TmbStatus validatePtesPort(String crmId, DcaValidationRequest dcaValidationRequest, Map<String, String> invHeaderReqParameter, TmbStatus tmbStatus) throws JsonProcessingException {

        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "getPtesPort", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.halfCrmIdFormat(crmId));
        ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> ptesPort = investmentRequestClient.getPtesPort(invHeaderReqParameter, UtilMap.halfCrmIdFormat(crmId));
        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "getPtesPort", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(ptesPort.getBody()));

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

    /**
     * Generic Method to validate alternative cases of dca
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param processFlag   the process flag
     * @return TmbOneServiceResponse<String>
     */
    @LogAround
    public TmbOneServiceResponse<String> validationAlternativeDca(String correlationId, String crmId, String processFlag) {
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            CustomerSearchResponse customerInfo = customerService.getCustomerInfo(correlationId, crmId);
            TmbStatus status = TmbStatusUtil.successStatus();
            tmbOneServiceResponse.setStatus(status);

            tmbOneServiceResponse = validateProcessFlag(processFlag, tmbOneServiceResponse, status);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            return validateBuyAndDca(correlationId, crmId, customerInfo, tmbOneServiceResponse, status, false, false);
        } catch (Exception ex) {
            logger.error("error : {}", ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }
}
