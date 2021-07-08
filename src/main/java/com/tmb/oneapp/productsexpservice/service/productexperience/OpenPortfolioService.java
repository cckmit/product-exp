package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * OpenPortfolioService class will get data from api services, and handle business criteria
 */
@Service
public class OpenPortfolioService {

    private static final TMBLogger<OpenPortfolioService> logger = new TMBLogger<>(OpenPortfolioService.class);

    private CommonServiceClient commonServiceClient;

    private InvestmentRequestClient investmentRequestClient;

    private InvestmentAsyncService investmentAsyncService;

    private OpenPortfolioMapper openPortfolioMapper;

    @Autowired
    public OpenPortfolioService(CommonServiceClient commonServiceClient, InvestmentRequestClient investmentRequestClient, InvestmentAsyncService investmentAsyncService, OpenPortfolioMapper openPortfolioMapper) {
        this.commonServiceClient = commonServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.investmentAsyncService = investmentAsyncService;
        this.openPortfolioMapper = openPortfolioMapper;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioValidationRequest
     */
    public ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> validateOpenPortfolio(String correlationId, OpenPortfolioValidationRequest openPortfolioValidationRequest) {
        return commonServiceClient.getTermAndConditionByServiceCodeAndChannel(correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
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
                return OpenPortfolioValidationResponse.builder()
                        .accountPurposeResponse(fetchAccountPurpose.get())
                        .accountRedeemResponse(fetchAccountRedeem.get())
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
}
