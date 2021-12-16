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
import feign.FeignException;
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
     * Generic Method to create new customer for open portfolio
     *
     * @param correlationId   the correlation id
     * @param crmId           the crm id
     * @param ipAddress       the ip address
     * @param customerRequest the customer request
     * @return OpenPortfolioValidationResponse
     */
    @LogAround
    public OpenPortfolioValidationResponse createCustomer(String correlationId, String crmId, String ipAddress, CustomerRequest customerRequest) throws TMBCommonException {
        try {
            openPortfolioActivityLogService.acceptTermAndCondition(correlationId, crmId, ipAddress);

            Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "createCustomer", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(customerRequest));
            ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> clientCustomer = investmentRequestClient.createCustomer(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId), customerRequest);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "createCustomer", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(clientCustomer.getBody()));

            if (HttpStatus.OK.equals(clientCustomer.getStatusCode())) {
                CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);
                CompletableFuture<OccupationInquiryResponseBody> occupationInquiry = investmentAsyncService.fetchOccupationInquiry(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
                CompletableFuture.allOf(fetchAccountPurpose, occupationInquiry);

                AccountPurposeResponseBody accountPurposeResponseBody = fetchAccountPurpose.get();
                OccupationInquiryResponseBody occupationInquiryResponseBody = occupationInquiry.get();

                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fetchAccountPurpose", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(accountPurposeResponseBody));
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fetchOccupationInquiry", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(occupationInquiryResponseBody));

                DepositAccount depositAccount = null;
                if (customerRequest.isExistingCustomer()) {
                    depositAccount = getDepositAccountForExistingCustomer(correlationId, investmentRequestHeader, crmId);
                }

                return OpenPortfolioValidationResponse.builder()
                        .accountPurposeResponse(accountPurposeResponseBody)
                        .depositAccount(depositAccount)
                        .occupationInquiryResponse(occupationInquiryResponseBody)
                        .build();
            }
        } catch (TMBCommonException e) {
            throw e;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TMBCommonException) {
                throw (TMBCommonException) e.getCause();
            }
        } catch (FeignException feignException) {
            handleFeignException(feignException);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return null;
    }

    @LogAround
    private DepositAccount getDepositAccountForExistingCustomer(String correlationId, Map<String, String> investmentRequestHeader, String crmId) throws JsonProcessingException {

        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fetchAccountRedeem", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.halfCrmIdFormat(crmId));
        ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> fetchAccountRedeem = investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, UtilMap.halfCrmIdFormat(crmId));
        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "fetchAccountRedeem", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(fetchAccountRedeem.getBody()));

        AccountRedeemResponseBody accountRedeem = fetchAccountRedeem.getBody().getData();
        String accountNumber = accountRedeem.getAccountRedeem();
        String accountType = UtilMap.getAccountTypeFromAccountNumber(accountNumber);

        AccountDetailRequest accountDetailRequest = AccountDetailRequest.builder()
                .accountNo(accountNumber)
                .accountType(accountType)
                .build();
        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER, "getAccountDetail", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(accountDetailRequest));
        String accountAccountSavingDetail = customerExpServiceClient.getAccountDetail(
                correlationId, accountDetailRequest);

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
     * Generic Method to open portfolio by calling MF service
     *
     * @param correlationId            the correlation id
     * @param crmId                    the crm id
     * @param ipAddress                the ip address
     * @param openPortfolioRequestBody the open portfolio request
     * @return PortfolioResponse
     */
    @LogAround
    public PortfolioResponse openPortfolio(String correlationId, String crmId, String ipAddress,
                                           OpenPortfolioRequestBody openPortfolioRequestBody) throws TMBCommonException {
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
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "updateOccupation", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(occupationResponseBody));
            }

            openPortfolioActivityLogService.enterPinIsCorrect(correlationId, crmId, ipAddress, ProductsExpServiceConstant.SUCCESS, openPortfolio.get().getPortfolioNumber(), openPortfolioRequestBody.getPortfolioNickName());

            OpenPortfolioResponseBody openPortfolioResponseBody = openPortfolio.get();
            RelationshipResponseBody relationshipResponseBody = relationship.get();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "openPortfolio", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(openPortfolioResponseBody));
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "clientRelationship", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(relationshipResponseBody));

            PortfolioNicknameRequest portfolioNicknameRequest = PortfolioNicknameRequest.builder()
                    .portfolioNumber(openPortfolioResponseBody.getPortfolioNumber())
                    .portfolioNickName(openPortfolioRequestBody.getPortfolioNickName())
                    .build();
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "portfolioNickname", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(portfolioNicknameRequest));
            ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> portfolioNickname = investmentRequestClient.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "portfolioNickname", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(portfolioNickname.getBody()));

            String fullCrmId = UtilMap.fullCrmIdFormat(crmId);
            removeCacheAfterSuccessOpenPortfolio(correlationId, fullCrmId);

            return PortfolioResponse.builder()
                    .relationshipResponse(relationshipResponseBody)
                    .openPortfolioResponse(openPortfolioResponseBody)
                    .portfolioNicknameResponse(portfolioNickname.getBody().getData())
                    .occupationResponse(occupationResponseBody != null ? occupationResponseBody : null)
                    .build();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TMBCommonException) {
                throw (TMBCommonException) e.getCause();
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            openPortfolioActivityLogService.enterPinIsCorrect(correlationId, crmId, ipAddress, ProductsExpServiceConstant.FAILED, "", openPortfolioRequestBody.getPortfolioNickName());
        }
        return null;
    }

    @LogAround
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
