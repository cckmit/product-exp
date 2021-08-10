package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInformationMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.tmb.oneapp.productsexpservice.util.ExceptionUtil.throwTmbException;
import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.successStatus;

@Service
public class OpenPortfolioValidationService {

    private static final TMBLogger<OpenPortfolioValidationService> logger = new TMBLogger<>(OpenPortfolioValidationService.class);

    private CustomerServiceClient customerServiceClient;

    private CommonServiceClient commonServiceClient;

    private EligibleDepositAccountService eligibleDepositAccountService;

    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    private CustomerInformationMapper customerInformationMapper;

    private AlternativeService alternativeService;

    @Autowired
    public OpenPortfolioValidationService(
            CustomerServiceClient customerServiceClient,
            CommonServiceClient commonServiceClient,
            EligibleDepositAccountService eligibleDepositAccountService,
            OpenPortfolioActivityLogService openPortfolioActivityLogService,
            CustomerInformationMapper customerInformationMapper,
            AlternativeService alternativeService) {

        this.customerServiceClient = customerServiceClient;
        this.commonServiceClient = commonServiceClient;
        this.eligibleDepositAccountService = eligibleDepositAccountService;
        this.openPortfolioActivityLogService = openPortfolioActivityLogService;
        this.customerInformationMapper = customerInformationMapper;
        this.alternativeService = alternativeService;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioValidateRequest
     */
    public TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateOpenPortfolioService(String correlationId, String crmId, OpenPortfolioValidationRequest openPortfolioValidateRequest) {
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfoFuture =
                    customerServiceClient.customerSearch(correlationId, crmId, CrmSearchBody.builder().searchType(ProductsExpServiceConstant.SEARCH_TYPE).searchValue(crmId).build());
            validateCustomerService(customerInfoFuture);
            CustomerSearchResponse customerInfo = customerInfoFuture.getBody().getData().get(0);

            List<DepositAccount> depositAccountList = null;
            if (!openPortfolioValidateRequest.isExistingCustomer()) {
                depositAccountList = eligibleDepositAccountService.getEligibleDepositAccounts(correlationId, crmId);
            }

            validateAlternativeCase(correlationId, crmId, customerInfo, depositAccountList, tmbOneServiceResponse);

            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return tmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> termAndCondition = commonServiceClient.getTermAndConditionByServiceCodeAndChannel(
                    correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
            if (!termAndCondition.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(termAndCondition.getBody().getData())) {
                throwTmbException("========== failed get termandcondition service ==========");
            }

            mappingOpenPortFolioValidationResponse(tmbOneServiceResponse, customerInfo, termAndCondition.getBody().getData(), depositAccountList);
            return tmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    private TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateAlternativeCase(
            String correlationId,
            String crmId,
            CustomerSearchResponse customerInfo,
            List<DepositAccount> depositAccountList,
            TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse) {

        TmbStatus status = TmbStatusUtil.successStatus();
        tmbOneServiceResponse.setStatus(successStatus());
        // validate service hour
        tmbOneServiceResponse.setStatus(alternativeService.validateServiceHour(correlationId, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
            return tmbOneServiceResponse;
        }

        // validate age should > 20
        tmbOneServiceResponse.setStatus(alternativeService.validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.AGE_NOT_OVER_TWENTY.getMsg());
            return tmbOneServiceResponse;
        }

        // validate account active only once
        tmbOneServiceResponse.setStatus(alternativeService.validateCasaAccountActiveOnce(depositAccountList, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMsg());
            return tmbOneServiceResponse;
        }

        // validate complete flatca form
        tmbOneServiceResponse.setStatus(alternativeService.validateFatcaFlagNotValid(customerInfo.getFatcaFlag(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
            return tmbOneServiceResponse;
        }

        // validate customer pass kyc (U,Blank) allow  and id card has not expired
        tmbOneServiceResponse.setStatus(alternativeService.validateKycAndIdCardExpire(customerInfo.getKycLimitedFlag(), customerInfo.getExpiryDate(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.FAILED_VERIFY_KYC.getMsg());
            return tmbOneServiceResponse;
        }

        // validate customer assurange level
        tmbOneServiceResponse.setStatus(alternativeService.validateIdentityAssuranceLevel(customerInfo.getEkycIdentifyAssuranceLevel(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg());
            return tmbOneServiceResponse;
        }

        // validate customer not us and not restriced in 30 nationality
        tmbOneServiceResponse.setStatus(alternativeService.validateNationality(correlationId, customerInfo.getNationality(), customerInfo.getNationalitySecond(), status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg());
            return tmbOneServiceResponse;
        }

        // validate customer risk level
        tmbOneServiceResponse.setStatus(alternativeService.validateCustomerRiskLevel(correlationId,customerInfo, status));
        if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
            openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO, AlternativeErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
            return tmbOneServiceResponse;
        }

        openPortfolioActivityLogService.openPortfolio(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_YES, "");
        return tmbOneServiceResponse;
    }

    private void mappingOpenPortFolioValidationResponse(TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse, CustomerSearchResponse customerInfo, TermAndConditionResponseBody termAndCondition, List<DepositAccount> depositAccountList) {
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder()
                .termsConditions(termAndCondition)
                .customerInformation(customerInformationMapper.map(customerInfo))
                .depositAccountList(depositAccountList)
                .build());
    }

    private void validateCustomerService(ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfo) throws TMBCommonException {
        if (!customerInfo.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(customerInfo.getBody().getData())) {
            throwTmbException("========== failed customer search service ==========");
        }
    }
}