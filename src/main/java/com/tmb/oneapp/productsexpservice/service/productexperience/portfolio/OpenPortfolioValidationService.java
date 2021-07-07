package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
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

    private ProductsExpService productsExpService;

    private EligibleDepositAccountService eligibleDepositAccountService;

    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    public OpenPortfolioValidationService(CustomerServiceClient customerServiceClient, CommonServiceClient commonServiceClient, ProductsExpService productsExpService, EligibleDepositAccountService eligibleDepositAccountService, CustomerInfoMapper customerInfoMapper) {
        this.customerServiceClient = customerServiceClient;
        this.commonServiceClient = commonServiceClient;
        this.productsExpService = productsExpService;
        this.eligibleDepositAccountService = eligibleDepositAccountService;
        this.customerInfoMapper = customerInfoMapper;
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
                depositAccountList = eligibleDepositAccountService.getEligibleDepositAccounts(correlationId, crmID);
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

    private void mappingOpenPortFolioValidationResponse(TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse, CustomerSearchResponse customerInfo, TermAndConditionResponseBody termAndCondition, List<DepositAccount> depositAccountList) {
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder()
                .termsConditions(termAndCondition)
                .customerInfo(customerInfoMapper.map(customerInfo))
                .depositAccountList(depositAccountList)
                .build());
    }

    private void validateCustomerService(ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfo) throws TMBCommonException {
        if (!customerInfo.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(customerInfo.getBody().getData())) {
            throwTmbException("========== failed customer search service ==========");
        }
    }
}
