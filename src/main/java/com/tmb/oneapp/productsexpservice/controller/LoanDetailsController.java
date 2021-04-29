package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.loan.AccountId;
import com.tmb.oneapp.productsexpservice.model.loan.LoanDetailsFullResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Payment;
import com.tmb.oneapp.productsexpservice.model.loan.Rates;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Credit Card-Cash For You")
public class LoanDetailsController {
    private static final TMBLogger<LoanDetailsController> log = new TMBLogger<>(LoanDetailsController.class);
    private final AccountRequestClient accountRequestClient;
    private final CommonServiceClient commonServiceClient;
    private final CreditCardLogService creditCardLogService;

    /**
     * Constructor
     *
     * @param accountRequestClient
     * @param commonServiceClient
     * @param creditCardLogService
     */
    @Autowired
    public LoanDetailsController(AccountRequestClient accountRequestClient, CommonServiceClient commonServiceClient,
                                 CreditCardLogService creditCardLogService) {
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
        this.creditCardLogService = creditCardLogService;
    }

    /**
     * @param requestHeadersParameter
     * @param requestBody
     * @return
     */
    @LogAround
    @PostMapping(value = "/loan/get-account-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> getLoanAccountDetail(
            @ApiParam(value = "X_CORRELATION_ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") @Valid @RequestHeader Map<String, String> requestHeadersParameter,
            @ApiParam(value = "Account ID", defaultValue = "00016109738001", required = true) @RequestBody AccountId requestBody) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanDetailsFullResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID);
        String activityDate = Long.toString(System.currentTimeMillis());
        String activityId = ProductsExpServiceConstant.ACTIVITY_ID_VIEW_LOAN_LENDING_SCREEN;
        CreditCardEvent creditCardEvent = new CreditCardEvent(
                requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID.toLowerCase()), activityDate,
                activityId);

        try {

            String accountId = requestBody.getAccountNo();
            if (!Strings.isNullOrEmpty(accountId)) {
                ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> loanResponse = accountRequestClient
                        .getLoanAccountDetail(correlationId, requestBody);
                int statusCodeValue = loanResponse.getStatusCodeValue();
                HttpStatus statusCode = loanResponse.getStatusCode();

                if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                    LoanDetailsFullResponse loanDetails = loanResponse.getBody().getData();
                    String productId = loanResponse.getBody().getData().getAccount().getProductId();
                    Rates rates = loanResponse.getBody().getData().getAccount().getRates();
                    return getTmbOneServiceResponseResponseEntity(requestHeadersParameter, responseHeaders, oneServiceResponse, correlationId, creditCardEvent, loanDetails, productId, rates);
                } else {
                    return getTmbOneServiceResponse(responseHeaders, oneServiceResponse);

                }
            } else {
                return getTmbOneServiceResponse(responseHeaders, oneServiceResponse);
            }

        } catch (Exception e) {
            log.error("Error while getLoanAccountDetails: {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }

    }

    ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> getTmbOneServiceResponse(HttpHeaders responseHeaders, TmbOneServiceResponse<LoanDetailsFullResponse> oneServiceResponse) {
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
    }

    ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> getTmbOneServiceResponseResponseEntity(Map<String, String> requestHeadersParameter, HttpHeaders responseHeaders, TmbOneServiceResponse<LoanDetailsFullResponse> oneServiceResponse, String correlationId, CreditCardEvent creditCardEvent, LoanDetailsFullResponse loanDetails, String productId, Rates rates) {
        String currentInterestRate = rates.getCurrentInterestRate();
        String originalInterestRate = rates.getOriginalInterestRate();
        String monthlyPaymentAmount = loanDetails.getAccount().getPayment().getMonthlyPaymentAmount();
        Double monthlyPayment = ConversionUtil.stringToDouble(monthlyPaymentAmount);
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        Payment payment = loanDetails.getAccount().getPayment();

        String formattedPayment = df.format(monthlyPayment);
        payment.setMonthlyPaymentAmount(formattedPayment);
        String currentInterestRateInPercent = currentInterestRate.concat(" %");
        String originalInterestRateInPercent = originalInterestRate.concat(" %");
        rates.setCurrentInterestRate(currentInterestRateInPercent);
        rates.setOriginalInterestRate(originalInterestRateInPercent);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> fetchProductConfigList = commonServiceClient
                .getProductConfig(correlationId);

        List<ProductConfig> list = fetchProductConfigList.getBody().getData();
        Iterator<ProductConfig> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProductConfig productConfig = iterator.next();
            if (productConfig.getProductCode().equalsIgnoreCase(productId)) {
                loanDetails.setProductConfig(productConfig);
            }
        }

        /* Activity log */
        creditCardEvent = creditCardLogService.viewLoanLandingScreenEvent(creditCardEvent,
                requestHeadersParameter, loanDetails);
        creditCardLogService.logActivity(creditCardEvent);
        oneServiceResponse
                .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                        ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        oneServiceResponse.setData(loanDetails);
        return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
    }
}
