package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsynService;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * OpenPortfolioService class will get data from api services, and handle business criteria
 */
@Service
public class OpenPortfolioService {

    private static final TMBLogger<OpenPortfolioService> logger = new TMBLogger<>(OpenPortfolioService.class);

    private AccountRequestClient accountRequestClient;

    private CustomerServiceClient customerServiceClient;

    private CommonServiceClient commonServiceClient;

    private InvestmentRequestClient investmentRequestClient;

    private ProductsExpService productsExpService;

    private InvestmentAsyncService investmentAsyncService;

    private ProductExpAsynService productExpAsynService;

    private CustomerInfoMapper customerInfoMapper;

    private OpenPortfolioMapper openPortfolioMapper;

    @Autowired
    public OpenPortfolioService(AccountRequestClient accountRequestClient, CustomerServiceClient customerServiceClient, CommonServiceClient commonServiceClient, InvestmentRequestClient investmentRequestClient, ProductsExpService productsExpService, InvestmentAsyncService investmentAsyncService, ProductExpAsynService productExpAsynService, CustomerInfoMapper customerInfoMapper, OpenPortfolioMapper openPortfolioMapper) {
        this.accountRequestClient = accountRequestClient;
        this.customerServiceClient = customerServiceClient;
        this.commonServiceClient = commonServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.productsExpService = productsExpService;
        this.investmentAsyncService = investmentAsyncService;
        this.productExpAsynService = productExpAsynService;
        this.customerInfoMapper = customerInfoMapper;
        this.openPortfolioMapper = openPortfolioMapper;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioValidateRequest
     */
    public TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateOpenPortfolioService(String correlationId, OpenPortfolioValidationRequest openPortfolioValidateRequest) {
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            String crmID = openPortfolioValidateRequest.getCrmId();
            FundResponse fundResponse = new FundResponse();
            fundResponse = productsExpService.isServiceHour(correlationId, fundResponse);
            if (fundResponse.isError()) {
                TmbStatus status = new TmbStatus();
                status.setCode(fundResponse.getErrorCode());
                status.setDescription(fundResponse.getErrorDesc());
                status.setMessage(fundResponse.getErrorMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                tmbOneServiceResponse.setStatus(status);
                return tmbOneServiceResponse;
            }
            CustomerSearchResponse customerInfo;
            List<DepositAccount> depositAccountList = null;

            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfoFuture =
                    customerServiceClient.customerSearch(crmID, correlationId, CrmSearchBody.builder().searchType(ProductsExpServiceConstant.SEARCH_TYPE).searchValue(crmID).build());
            validateCustomerService(customerInfoFuture);
            customerInfo = customerInfoFuture.getBody().getData().get(0);

            if (!openPortfolioValidateRequest.isExistingCustomer()) {
                depositAccountList = getEligibleDepositAccounts(correlationId, crmID);
            }

            ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> termAndCondition = commonServiceClient.getTermAndConditionByServiceCodeAndChannel(
                    correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
            if (!termAndCondition.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(termAndCondition.getBody().getData())) {
                throwTmbException("========== failed get termandcondition service ==========");
            }

            tmbOneServiceResponse.setStatus(successStatus());
            mappingOpenPortFolioValidationResponse(tmbOneServiceResponse, customerInfo, termAndCondition.getBody().getData(), depositAccountList);
            return tmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    /**
     * Method createCustomer
     *
     * @param correlationId
     * @param customerRequest
     */
    public OpenPortfolioValidationResponse createCustomer(String correlationId, CustomerRequest customerRequest) {
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> clientCustomer = investmentRequestClient.createCustomer(investmentRequestHeader, customerRequest);
        if (HttpStatus.OK.equals(clientCustomer.getStatusCode())) {
            try {
                AccountRedeemRequest accountRedeemRequest = AccountRedeemRequest.builder().crmId(customerRequest.getCrmId()).build();
                CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);
                CompletableFuture<AccountRedeemResponseBody> fetchAccountRedeem = investmentAsyncService.fetchAccountRedeem(investmentRequestHeader, accountRedeemRequest);
                CompletableFuture.allOf(fetchAccountPurpose, fetchAccountRedeem);

                AccountRedeemResponseBody accountRedeem = fetchAccountRedeem.get();
                List<DepositAccount> eligibleDepositAccounts = getEligibleDepositAccounts(correlationId, accountRedeem.getCrmId());
                Optional<DepositAccount> account = eligibleDepositAccounts.stream()
                        .filter(depositAccount -> accountRedeem.getAccountRedeem().equals(depositAccount.getAccountNumber()))
                        .findFirst();

                if (account.isEmpty()) {
                    throwTmbException("========== failed account return 0 in list ==========");
                }

                return OpenPortfolioValidationResponse.builder()
                        .accountPurposeResponse(fetchAccountPurpose.get())
                        .depositAccount(account.get())
                        .build();
            } catch (Exception ex) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
                return null;
            }
        }
        return null;
    }

    /**
     * Method openPortfolio
     *
     * @param correlationId
     * @param openPortfolioRequestBody
     */
    public PortfolioResponse openPortfolio(String correlationId, OpenPortfolioRequestBody openPortfolioRequestBody) throws TMBCommonException {
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
        try {
            RelationshipRequest relationshipRequest = openPortfolioMapper.openPortfolioRequestBodyToRelationshipRequest(openPortfolioRequestBody);
            CompletableFuture<RelationshipResponseBody> relationship = investmentAsyncService.updateClientRelationship(investmentRequestHeader, relationshipRequest);

            OpenPortfolioRequest openPortfolioRequest = openPortfolioMapper.openPortfolioRequestBodyToOpenPortfolioRequest(openPortfolioRequestBody);
            CompletableFuture<OpenPortfolioResponseBody> openPortfolio = investmentAsyncService.openPortfolio(investmentRequestHeader, openPortfolioRequest);

            PortfolioNicknameRequest portfolioNicknameRequest = openPortfolioMapper.openPortfolioRequestBodyToPortfolioNicknameRequest(openPortfolioRequestBody);
            CompletableFuture<PortfolioNicknameResponseBody> portfolioNickname = investmentAsyncService.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);

            CompletableFuture.allOf(relationship, openPortfolio, portfolioNickname);

            return PortfolioResponse.builder()
                    .relationshipResponse(relationship.get())
                    .openPortfolioResponse(openPortfolio.get())
                    .portfolioNicknameResponse(portfolioNickname.get())
                    .build();
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
    }

    private List<DepositAccount> getEligibleDepositAccounts(String correlationId, String crmID) throws Exception {
        List<DepositAccount> depositAccountList;
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsynService.fetchCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
        CompletableFuture<String> accountInfo =
                CompletableFuture.completedFuture(accountRequestClient.callCustomerExpService(UtilMap.createHeader(correlationId), crmID));
        CompletableFuture.allOf(fetchCommonConfigByModule, accountInfo);

        UtilMap utilMap = new UtilMap();
        depositAccountList = utilMap.mappingAccount(fetchCommonConfigByModule.get(), accountInfo.get());
        validateAccountList(depositAccountList);
        return depositAccountList;
    }

    private void mappingOpenPortFolioValidationResponse(TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse, CustomerSearchResponse customerInfo, TermAndConditionResponseBody termAndCondition, List<DepositAccount> depositAccountList) {
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder()
                .termsConditions(termAndCondition)
                .customerInfo(customerInfoMapper.map(customerInfo))
                .depositAccountList(depositAccountList)
                .build());
    }

    private void validateCustomerService(ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfo) throws Exception {
        if (!customerInfo.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(customerInfo.getBody().getData())) {
            throwTmbException("========== failed customer search service ==========");
        }
    }

    private void validateAccountList(List<DepositAccount> depositAccountList) throws Exception {
        if (depositAccountList.isEmpty()) {
            throwTmbException("========== failed account return 0 in list ==========");
        }
    }

    private void throwTmbException(String message) throws TMBCommonException {
        throw new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                message,
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);
    }

    private TmbStatus successStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.SUCCESS_CODE);
        status.setDescription(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }
}
