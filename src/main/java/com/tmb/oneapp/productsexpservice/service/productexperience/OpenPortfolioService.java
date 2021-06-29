package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequestBody;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.OpenPortfolioResponse;
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

    @Autowired
    public OpenPortfolioService(CommonServiceClient commonServiceClient, InvestmentRequestClient investmentRequestClient, InvestmentAsyncService investmentAsyncService) {
        this.commonServiceClient = commonServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.investmentAsyncService = investmentAsyncService;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioRequest
     */
    public ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> validateOpenPortfolio(String correlationId, OpenPortfolioRequest openPortfolioRequest) {
        return commonServiceClient.getTermAndConditionByServiceCodeAndChannel(correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
    }

    /**
     * Method createCustomer
     *
     * @param correlationId
     * @param customerRequestBody
     */
    public OpenPortfolioResponse createCustomer(String correlationId, CustomerRequestBody customerRequestBody) {
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> clientCustomer = investmentRequestClient.createCustomer(investmentRequestHeader, customerRequestBody);
        if (HttpStatus.OK.equals(clientCustomer.getStatusCode())) {
            try {
                AccountRedeemRequest accountRedeemRequest = AccountRedeemRequest.builder().crmId(customerRequestBody.getCrmId()).build();
                CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);
                CompletableFuture<AccountRedeemResponseBody> fetchAccountRedeem = investmentAsyncService.fetchAccountRedeem(investmentRequestHeader, accountRedeemRequest);
                CompletableFuture.allOf(fetchAccountPurpose, fetchAccountRedeem);
                return OpenPortfolioResponse.builder()
                        .accountPurposeResponseBody(fetchAccountPurpose.get())
                        .accountRedeemResponseBody(fetchAccountRedeem.get())
                        .build();
            } catch (Exception ex) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
                return null;
            }
        }
        return null;
    }
}
