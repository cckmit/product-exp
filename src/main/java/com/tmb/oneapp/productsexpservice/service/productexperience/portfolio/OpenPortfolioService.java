package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.customer.accountdetail.request.AccountDetailRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * OpenPortfolioService class will get data from api services, and handle business criteria
 */
@Service
public class OpenPortfolioService extends TmbErrorHandle {

    private static final TMBLogger<OpenPortfolioService> logger = new TMBLogger<>(OpenPortfolioService.class);

    private InvestmentRequestClient investmentRequestClient;

    private InvestmentAsyncService investmentAsyncService;

    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    private OpenPortfolioMapper openPortfolioMapper;

    private CustomerExpServiceClient customerExpServiceClient;

    private CacheServiceClient cacheServiceClient;

    @Autowired
    public OpenPortfolioService(
            InvestmentRequestClient investmentRequestClient,
            InvestmentAsyncService investmentAsyncService,
            OpenPortfolioActivityLogService openPortfolioActivityLogService,
            OpenPortfolioMapper openPortfolioMapper,
            CustomerExpServiceClient customerExpServiceClient,
            CacheServiceClient cacheServiceClient) {

        this.investmentRequestClient = investmentRequestClient;
        this.investmentAsyncService = investmentAsyncService;
        this.openPortfolioActivityLogService = openPortfolioActivityLogService;
        this.openPortfolioMapper = openPortfolioMapper;
        this.customerExpServiceClient = customerExpServiceClient;
        this.cacheServiceClient = cacheServiceClient;

    }

    /**
     * Method createCustomer
     *
     * @param correlationId
     * @param customerRequest
     */
    @LogAround
    public OpenPortfolioValidationResponse createCustomer(String correlationId, String crmId, CustomerRequest customerRequest) throws TMBCommonException {
        try {
            openPortfolioActivityLogService.acceptTermAndCondition(correlationId, crmId, ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_ACCEPT_TERM_AND_CONDITION);

            Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
            ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> clientCustomer = investmentRequestClient.createCustomer(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), customerRequest);
            if (HttpStatus.OK.equals(clientCustomer.getStatusCode())) {
                CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);
                CompletableFuture<OccupationInquiryResponseBody> occupationInquiry = investmentAsyncService.fetchOccupationInquiry(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
                CompletableFuture.allOf(fetchAccountPurpose, occupationInquiry);

                DepositAccount depositAccount = null;
                if (customerRequest.isExistingCustomer()) {
                    depositAccount = getDepositAccountForExisitngCustomer(correlationId, investmentRequestHeader, crmId);
                }

                return OpenPortfolioValidationResponse.builder()
                        .accountPurposeResponse(fetchAccountPurpose.get())
                        .depositAccount(depositAccount)
                        .occupationInquiryResponse(occupationInquiry.get())
                        .build();
            }
        } catch (TMBCommonException e) {
            throw e;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TMBCommonException) {
                throw (TMBCommonException) e.getCause();
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return null;
    }

    private DepositAccount getDepositAccountForExisitngCustomer(String correlationId, Map<String, String> investmentRequestHeader, String crmId) throws TMBCommonException {
        ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> fetchAccountRedeem = investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
        tmbResponseErrorHandle(fetchAccountRedeem.getBody().getStatus());
        AccountRedeemResponseBody accountRedeem = fetchAccountRedeem.getBody().getData();
        String accountNumber = accountRedeem.getAccountRedeem();
        String accountType = UtilMap.getAccountTypeFromAccountNumber(accountNumber);
        String accountAccountSavingDetail = customerExpServiceClient.getAccountDetail(
                correlationId, AccountDetailRequest.builder()
                        .accountNo(accountNumber)
                        .accountType(accountType)
                        .build());

        String productNameTh = "";
        String productNameEn = "";
        String accountNickName = "";
        String accountStatus = "";
        String accountBalance = "";

        try {
            JsonNode node;
            ObjectMapper mapper = new ObjectMapper();
            node = mapper.readValue(accountAccountSavingDetail, JsonNode.class);
            JsonNode dataNode = node.get("data");
            productNameTh = dataNode.get("productNameTh").textValue();
            productNameEn = dataNode.get("productNameEn").textValue();
            accountNickName = dataNode.get("accountName").textValue();
            accountStatus = dataNode.get("accountStatus").textValue();
            accountBalance = dataNode.get("accountBalance").textValue();

        } catch (JsonProcessingException e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
        }

        return DepositAccount.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .accountStatus(accountStatus)
                .availableBalance(StringUtils.isEmpty(accountBalance) ? null : new BigDecimal(accountBalance))
                .productNickname(accountNickName)
                .productNameTH(productNameTh)
                .productNameEN(productNameEn)
                .build();
    }

    /**
     * Method openPortfolio
     *
     * @param correlationId
     * @param openPortfolioRequestBody
     */
    @LogAround
    public PortfolioResponse openPortfolio(String correlationId, String crmId, OpenPortfolioRequestBody openPortfolioRequestBody) throws TMBCommonException {
        OccupationResponseBody occupationResponseBody = null;
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);

        try {
            RelationshipRequest relationshipRequest = openPortfolioMapper.openPortfolioRequestBodyToRelationshipRequest(openPortfolioRequestBody);
            CompletableFuture<RelationshipResponseBody> relationship = investmentAsyncService.updateClientRelationship(
                    investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), relationshipRequest);

            OpenPortfolioRequest openPortfolioRequest = openPortfolioMapper.openPortfolioRequestBodyToOpenPortfolioRequest(openPortfolioRequestBody);
            CompletableFuture<OpenPortfolioResponseBody> openPortfolio = investmentAsyncService.openPortfolio(
                    investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), openPortfolioRequest);

            if (openPortfolioRequestBody.getOccupationRequest() == null) {
                CompletableFuture.allOf(relationship, openPortfolio);
            } else {
                CompletableFuture<OccupationResponseBody> occupation = investmentAsyncService.updateOccupation(
                        investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), openPortfolioRequestBody.getOccupationRequest());
                CompletableFuture.allOf(relationship, openPortfolio, occupation);
                occupationResponseBody = occupation.get();
            }

            openPortfolioActivityLogService.enterPinIsCorrect(correlationId, crmId, ProductsExpServiceConstant.SUCCESS, openPortfolio.get().getPortfolioNumber(), openPortfolioRequestBody.getPortfolioNickName());

            OpenPortfolioResponseBody openPortfolioResponseBody = openPortfolio.get();
            PortfolioNicknameRequest portfolioNicknameRequest = PortfolioNicknameRequest.builder()
                    .portfolioNumber(openPortfolioResponseBody.getPortfolioNumber())
                    .portfolioNickName(openPortfolioRequestBody.getPortfolioNickName())
                    .build();
            ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> portfolioNickname = investmentRequestClient.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);

            String fullCrmId = UtilMap.fullCrmIdFormat(crmId);
            removeCacheAfterSuccessOpenPortfolio(correlationId, fullCrmId);

            return PortfolioResponse.builder()
                    .relationshipResponse(relationship.get())
                    .openPortfolioResponse(openPortfolio.get())
                    .portfolioNicknameResponse(portfolioNickname.getBody().getData())
                    .occupationResponse(occupationResponseBody != null ? occupationResponseBody : null)
                    .build();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TMBCommonException) {
                throw (TMBCommonException) e.getCause();
            }
            failedErrorHandle();
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            openPortfolioActivityLogService.enterPinIsCorrect(correlationId, crmId, ProductsExpServiceConstant.FAILED, "", openPortfolioRequestBody.getPortfolioNickName());
        }
        return null;
    }

    private void removeCacheAfterSuccessOpenPortfolio(String correlationId, String fullCrmId) {
        String depositWithCrmIdKey = String.format("%s_deposit", fullCrmId);
        String investmentWithCrmIdKey = String.format("%s_investment", fullCrmId);

        try {
            cacheServiceClient.deleteCacheByKey(correlationId, depositWithCrmIdKey);
            cacheServiceClient.deleteCacheByKey(correlationId, investmentWithCrmIdKey);
            logger.info("========== remove cache success ==========");
        } catch (Exception ex) {
            logger.info("========== Can't Remove Key Redis complete ==========");
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
    }
}
