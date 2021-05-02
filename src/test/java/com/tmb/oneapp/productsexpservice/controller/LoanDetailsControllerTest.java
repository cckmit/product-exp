package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanDetailsControllerTest {

    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    LoanDetailsController homeLoanController;
    @Mock
    CommonServiceClient commonServiceClient;
    @Mock
    CreditCardLogService creditCardLogService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeLoanController = new LoanDetailsController(accountRequestClient, commonServiceClient, creditCardLogService);
    }

    @Test
    public void testGetLoanAccountDetail() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        DebitAccount account = new DebitAccount();
        account.setId("1234");
        account.setAutoDebitDate("12-12-2021");
        account.setAutoDebitMethod("card");
        accountId.setDebitAccount(account);
        response.setAccount(accountId);
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        response.setAccount(accountId);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> serviceResponse = new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId id = new AccountId();
        id.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, id);

        Assert.assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    public void testGetLoanAccountDetailElseCase() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        HomeLoanFullInfoResponse response = new HomeLoanFullInfoResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        response.setAccount(accountId);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> serviceResponse = new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, account);
        Assert.assertEquals(400, result.getStatusCodeValue());

    }

    @Test
    void testGetLoanAccountDetail1() {
        // Setup
        final Map<String, String> requestHeadersParameter = Map.ofEntries(Map.entry("value", "value"));

        final AccountId requestBody = new AccountId();
        requestBody.setAccountNo("accountNo");

        // Configure AccountRequestClient.getLoanAccountDetail(...).
        final TmbOneServiceResponse<HomeLoanFullInfoResponse> loanDetailsFullResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        loanDetailsFullResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final HomeLoanFullInfoResponse loanDetailsFullResponse = new HomeLoanFullInfoResponse();
        final StatusResponse status = new StatusResponse();
        status.setCode("code");
        status.setDescription("description");
        loanDetailsFullResponse.setStatus(status);
        final AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setStatusCode("statusCode");
        additionalStatus.setServerStatusCode("serverStatusCode");
        additionalStatus.setSeverity("severity");
        additionalStatus.setStatusDesc("statusDesc");
        loanDetailsFullResponse.setAdditionalStatus(additionalStatus);
        final Account account = new Account();
        account.setId("id");
        account.setType("type");
        account.setBranchId("branchId");
        account.setProductId("productId");
        account.setTitle("title");
        account.setCurrency("currency");
        final Status status1 = new Status();
        status1.setAccountStatus("accountStatus");
        status1.setContractDate("contractDate");
        account.setStatus(status1);
        final DebitAccount debitAccount = new DebitAccount();
        debitAccount.setId("id");
        debitAccount.setAutoDebitMethod("autoDebitMethod");
        debitAccount.setAutoDebitDate("autoDebitDate");
        account.setDebitAccount(debitAccount);
        final Balances balances = new Balances();
        balances.setOriginalLoan("originalLoan");
        balances.setPrincipal("principal");
        balances.setLedger("ledger");
        balances.setOutstanding("outstanding");
        balances.setAvailable("available");
        balances.setCurrent("current");
        balances.setAccruedInterest("accruedInterest");
        balances.setPayoff("payoff");
        account.setBalances(balances);
        final Payment payment = new Payment();
        payment.setMonthlyPaymentAmount("monthlyPaymentAmount");
        payment.setNextPaymentDueDate("nextPaymentDueDate");
        payment.setNextPaymentAmount("nextPaymentAmount");
        payment.setNextPaymentPrincipal("nextPaymentPrincipal");
        payment.setNextPaymentInterest("nextPaymentInterest");
        payment.setLastPaymentAmount("lastPaymentAmount");
        payment.setLastPaymentDate("lastPaymentDate");
        payment.setCurrentTerms("currentTerms");
        payment.setRemainingTerms("remainingTerms");
        payment.setTotalPaymentAmount("totalPaymentAmount");
        account.setPayment(payment);
        loanDetailsFullResponse.setAccount(account);
        final ProductConfig productConfig = new ProductConfig();
        productConfig.setProductCode("productCode");
        productConfig.setProductNameEN("productNameEN");
        productConfig.setProductNameTH("productNameTH");
        productConfig.setIconId("iconId");
        productConfig.setOpenEkyc("openEkyc");
        loanDetailsFullResponse.setProductConfig(productConfig);
        loanDetailsFullResponseTmbOneServiceResponse.setData(loanDetailsFullResponse);
        final ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(loanDetailsFullResponseTmbOneServiceResponse, HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(anyString(), any())).thenReturn(tmbOneServiceResponseEntity);

        // Configure CommonServiceClient.getProductConfig(...).
        final TmbOneServiceResponse<List<ProductConfig>> listTmbOneServiceResponse = new TmbOneServiceResponse<>();
        listTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final ProductConfig productConfig1 = new ProductConfig();
        productConfig1.setProductCode("productCode");
        productConfig1.setProductNameEN("productNameEN");
        productConfig1.setProductNameTH("productNameTH");
        productConfig1.setIconId("iconId");
        productConfig1.setOpenEkyc("openEkyc");
        listTmbOneServiceResponse.setData(List.of(productConfig1));
        final ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> tmbOneServiceResponseEntity1 = new ResponseEntity<>(listTmbOneServiceResponse, HttpStatus.OK);
        when(commonServiceClient.getProductConfig("correlationID")).thenReturn(tmbOneServiceResponseEntity1);

        // Configure CreditCardLogService.viewLoanLandingScreenEvent(...).
        final CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
        when(creditCardLogService.viewLoanLandingScreenEvent(any(), any(), any())).thenReturn(creditCardEvent);

        // Run the test
        final ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(requestHeadersParameter, requestBody);

        Assert.assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    public void testGetLoanAccountDetailsCase() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        Balances accountNo = new Balances();
        accountNo.setAccruedInterest("1234.00");
        accountNo.setCurrent("123");
        accountNo.setLedger("123");
        accountNo.setPrincipal("1234");
        accountId.setBalances(accountNo);
        accountId.setProductId("1234");
        accountId.setId("1234");
        CreditLimit limit = new CreditLimit();
        limit.setAmount("1234.00");
        limit.setExpiredDate("10-10-2021");
        limit.setInterestRate("1234.00");
        accountId.setCreditLimit(limit);
        Status stat = new Status();
        stat.setAccountStatus("123");
        stat.setContractDate("10-10-2021");
        accountId.setStatus(stat);
        accountId.setTitle("Test");
        accountId.setType("Test");
        Payment payment = new Payment();
        payment.setCurrentTerms("test");
        payment.setLastPaymentAmount("1234.00");
        payment.setPastDueDate("10-10-2021");
        accountId.setPayment(payment);
        DebitAccount debitAccount = new DebitAccount();
        debitAccount.setAutoDebitDate("06-06-2021");
        debitAccount.setAutoDebitMethod("card");
        debitAccount.setId("1234");
        accountId.setDebitAccount(debitAccount);

        response.setAccount(accountId);
        AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setStatusCode("1234");
        additionalStatus.setSeverity("Awesome");
        additionalStatus.setStatusDesc("successful");
        additionalStatus.setServerStatusCode("0");
        response.setAdditionalStatus(additionalStatus);
        ProductConfig productConfig = new ProductConfig();
        productConfig.setProductNameEN("Mobile");
        productConfig.setProductCode("1234");
        response.setProductConfig(productConfig);
        TmbOneServiceResponse<LoanDetailsFullResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setMessage("Successful");
        tmbStatus.setService("loan-service");
        tmbStatus.setDescription("Successful");
        tmbStatus.setCode("0");
        tmbOneServiceResponse.setData(response);
        tmbOneServiceResponse.setStatus(tmbStatus);

        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> serviceResponse = new ResponseEntity(tmbOneServiceResponse, HttpStatus.OK);
        TmbOneServiceResponse<List<ProductConfig>> listTmbOneServiceResponse = new TmbOneServiceResponse<>();
        listTmbOneServiceResponse.setStatus(tmbStatus);
        List<ProductConfig> list = new ArrayList<>();
        for (ProductConfig config : list) {
            config.setProductCode("123");
            config.setIconId("123");
            config.setOpenEkyc("123");
            config.setProductNameEN("TEST");
            list.add(config);
        }

        listTmbOneServiceResponse.setData(list);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> commonResponse = new ResponseEntity(listTmbOneServiceResponse, HttpStatus.OK);
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(commonResponse);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, account);
        Assert.assertNotNull(result.getStatusCodeValue());
    }

    @Test
    void testGetCreditCardDetailsNull() {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        response.setAccount(accountId);
        StatusResponse status = new StatusResponse();
        status.setCode("1234");
        status.setDescription("Sucessful");
        response.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, accountNo);

        assertNotNull(result);

    }

    @Test
    void testGetLoanDetailsNull() {
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> {
            accountRequestClient.getLoanAccountDetail(correlationId, accountNo);
        });
    }


    @Test
    void getTmbOneServiceResponseResponseEntity() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        TmbOneServiceResponse<LoanDetailsFullResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setMessage("test");
        tmbStatus.setService("test");
        tmbStatus.setCode("test");
        tmbStatus.setDescription("test");
        oneServiceResponse.setStatus(tmbStatus);
        LoanDetailsFullResponse data = new LoanDetailsFullResponse();
        Account account = new Account();
        DebitAccount debitAccount = new DebitAccount();
        debitAccount.setAutoDebitDate("test");
        debitAccount.setId("1234");
        debitAccount.setAutoDebitMethod("credit-card");
        account.setDebitAccount(debitAccount);
        data.setAccount(account);
        Payment payment = new Payment();
        payment.setMonthlyPaymentAmount("1234");
        account.setPayment(payment);
        oneServiceResponse.setData(data);
        LoanDetailsFullResponse loanDetails = new LoanDetailsFullResponse();
        loanDetails.setAccount(account);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setCode("0");
        statusResponse.setDescription("Success");
        loanDetails.setStatus(statusResponse);
        Rates rates = new Rates();
        rates.setCurrentInterestRate("1234");
        rates.setOriginalInterestRate("12334");
        TmbOneServiceResponse<List<ProductConfig>> resp = new TmbOneServiceResponse<>();
        List<ProductConfig> list = new ArrayList<>();
        ProductConfig config = new ProductConfig();
        config.setProductCode("1234");
        config.setProductNameEN("test");
        config.setOpenEkyc("1234");
        config.setIconId("1234");
        config.setProductNameTH("test");
        list.add(config);
        resp.setData(list);
        resp.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> responseEntity = new ResponseEntity<>(resp, HttpStatus.OK);
        when(commonServiceClient.getProductConfig(any())).thenReturn(responseEntity);
        Map<String, String> reqHeaders = headerRequestParameter("1234");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("1223");
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, accountNo);

        assertNotNull(result);

    }

    @Test
    void testLoanDetailsNull() {
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> {
            accountRequestClient.getLoanAccountDetail(correlationId, accountNo);
        });
    }


    @Test
    void testGetTmbOneServiceResponseResponseEntity() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setMessage("test");
        tmbStatus.setService("test");
        tmbStatus.setCode("test");
        tmbStatus.setDescription("test");
        oneServiceResponse.setStatus(tmbStatus);
        HomeLoanFullInfoResponse data = new HomeLoanFullInfoResponse();
        Account account = new Account();
        DebitAccount debitAccount = new DebitAccount();
        debitAccount.setAutoDebitDate("test");
        debitAccount.setId("1234");
        debitAccount.setAutoDebitMethod("credit-card");
        account.setDebitAccount(debitAccount);
        data.setAccount(account);
        oneServiceResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> tmbOneServiceResponse = homeLoanController.getFailedResponse(responseHeaders, oneServiceResponse);
        assertNotNull(tmbOneServiceResponse);
    }

    public Map<String, String> headerRequestParameter(String correlationId) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        return reqData;

    }

    @Test
    void testGetTmbOneServiceResponseRespons() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setMessage("test");
        tmbStatus.setService("test");
        tmbStatus.setCode("test");
        tmbStatus.setDescription("test");
        oneServiceResponse.setStatus(tmbStatus);
        HomeLoanFullInfoResponse data = new HomeLoanFullInfoResponse();
        Account account = new Account();
        DebitAccount debitAccount = new DebitAccount();
        debitAccount.setAutoDebitDate("test");
        debitAccount.setId("1234");
        debitAccount.setAutoDebitMethod("credit-card");
        account.setDebitAccount(debitAccount);
        data.setAccount(account);
        Payment payment = new Payment();
        payment.setMonthlyPaymentAmount("1234");
        account.setPayment(payment);
        oneServiceResponse.setData(data);
        Map<String, String> requestHeadersParameter = headerRequestParameter("1234");
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, "", "");
        HomeLoanFullInfoResponse loanDetails = new HomeLoanFullInfoResponse();
        loanDetails.setAccount(account);
        StatusResponse status = new StatusResponse();
        status.setCode("0");
        status.setDescription("Success");
        loanDetails.setStatus(status);
        Rates rates = new Rates();
        rates.setCurrentInterestRate("1234");
        rates.setOriginalInterestRate("12334");
        account.setRates(rates);
        TmbOneServiceResponse<List<ProductConfig>> resp = new TmbOneServiceResponse<>();
        List<ProductConfig> list = new ArrayList<>();
        ProductConfig config = new ProductConfig();
        config.setProductCode("1234");
        config.setProductNameEN("test");
        config.setOpenEkyc("1234");
        config.setIconId("1234");
        config.setProductNameTH("test");
        list.add(config);
        resp.setData(list);
        resp.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = new ResponseEntity<>(resp, HttpStatus.OK);
        when(commonServiceClient.getProductConfig(any())).thenReturn(response);
        TmbOneServiceResponse<HomeLoanFullInfoResponse> res = new TmbOneServiceResponse<>();
        res.setStatus(tmbStatus);
        res.setData(data);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> loanResponse = new ResponseEntity<>(res, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> responseEntity = homeLoanController.getTmbOneServiceResponseResponseEntity(requestHeadersParameter, responseHeaders, oneServiceResponse, correlationId, creditCardEvent, loanResponse);
        assertNotNull(responseEntity);
    }
}
