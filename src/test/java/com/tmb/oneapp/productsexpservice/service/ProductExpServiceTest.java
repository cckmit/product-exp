package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import com.tmb.oneapp.productsexpservice.model.response.investment.OrderToBeProcess;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductExpServiceTest {

    @Mock
    private AccountRequestClient accountRequestClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductExpAsyncService productExpAsyncService;

    @InjectMocks
    private ProductsExpService productsExpService;

    private AccountDetailResponse accountDetailResponse = null;

    private FundRuleResponse fundRuleResponse = null;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000012025950";

    private void initAccountDetailResponse() {
        accountDetailResponse = new AccountDetailResponse();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("TTTTT");
        fundDetail.setFundHouseCode("EEEEE");
        accountDetailResponse.setFundDetail(fundDetail);

        OrderToBeProcess orderToBeProcess = new OrderToBeProcess();
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setAmount("200");
        order.setOrderDate("20201212");
        orders.add(order);
        orderToBeProcess.setOrder(orders);
    }

    private void initFundRuleResponse() {
        fundRuleResponse = new FundRuleResponse();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleResponse.setFundRuleInfoList(fundRuleInfoList);
    }

    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        return invHeaderReqParameter;
    }

    @Test
    public void testGetFundAccountDetailAndFundRule() throws Exception {
        StatementResponse statementResponse;
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundHouseCode("ABCC");
        fundAccountRequest.setTranType("2");
        fundAccountRequest.setFundCode("ABCC");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT0000000000123");

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailResponse.class);
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleResponse.class);
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            when(productExpAsyncService.fetchFundAccountDetail(any(), any())).thenReturn(CompletableFuture.completedFuture(accountDetailResponse));
            when(productExpAsyncService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleResponse));
            when(productExpAsyncService.fetchStatementByPort(any(), any())).thenReturn(CompletableFuture.completedFuture(statementResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<AccountDetailResponse> fetchFundAccountDetail = productExpAsyncService.fetchFundAccountDetail(any(), any());
        CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(any(), any());
        CompletableFuture<StatementResponse> fetchStmtByPort = productExpAsyncService.fetchStatementByPort(any(), any());
        CompletableFuture.allOf(fetchFundAccountDetail, fetchFundRule, fetchStmtByPort);

        AccountDetailResponse accountDetailResponse = fetchFundAccountDetail.get();
        FundRuleResponse fundRuleResponse = fetchFundRule.get();
        StatementResponse fetchStatementResponse = fetchStmtByPort.get();

        FundAccountResponse fundAccountResponse = UtilMap.validateTMBResponse(accountDetailResponse, fundRuleResponse, fetchStatementResponse);

        Assert.assertNotNull(fundAccountResponse);
        Assert.assertNotNull(accountDetailResponse);
        Assert.assertNotNull(fetchStatementResponse);
        FundAccountResponse result = productsExpService.getFundAccountDetail(correlationId, fundAccountRequest);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFundAccountDetail() {
        FundAccountRequestBody accountRequestBody = new FundAccountRequestBody();
        accountRequestBody.setPortfolioNumber("PT000000001");
        accountRequestBody.setFundCode("DDD");

        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailResponse.class);

            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(correlationId), accountRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<AccountDetailResponse>> responseEntity = investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(correlationId), accountRequestBody);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertEquals(responseEntity.getBody().getData(), responseEntity.getBody().getData());
        Assert.assertNotNull(responseEntity.getBody().getData().getFundDetail());
    }

    @Test
    public void testGetFundRule() {
        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("2");
        fundRuleRequestBody.setFundHouseCode("TTTTT");
        fundRuleRequestBody.setFundCode("EEEEE");

        TmbOneServiceResponse<FundRuleResponse> oneServiceResponseBody = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<FundRuleResponse>> fundRuleResponseEntity;

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleResponse.class);
            oneServiceResponseBody.setData(fundRuleResponse);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(correlationId), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fundRuleResponseEntity = investmentRequestClient.callInvestmentFundRuleService(createHeader(correlationId), fundRuleRequestBody);
        Assert.assertEquals(HttpStatus.OK, fundRuleResponseEntity.getStatusCode());
        Assert.assertEquals("TESEQDSSFX", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundCode());
        Assert.assertEquals("TFUND", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundHouseCode());
        Assert.assertEquals("20200413", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getTranStartDate());
        Assert.assertEquals("3", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getOrderType());
        Assert.assertEquals("3", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getAllotType());
        Assert.assertEquals("06", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getRiskRate());
    }

    @Test
    public void testGetFundAccountDetailNull() throws TMBCommonException {
        initAccountDetailResponse();
        initFundRuleResponse();
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        FundAccountRequestBody fundAccountRequestBody = new FundAccountRequestBody();
        fundAccountRequestBody.setPortfolioNumber("PT000000000000138924");
        fundAccountRequestBody.setFundCode("DDD");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("1");
        fundRuleRequestBody.setFundHouseCode("TFUND");
        fundRuleRequestBody.setFundCode("TMB50");

        OrderStmtByPortRequest orderStmtByPortRequest = new OrderStmtByPortRequest();
        orderStmtByPortRequest.setPortfolioNumber("PT0000000032534");
        orderStmtByPortRequest.setRowEnd("5");
        orderStmtByPortRequest.setRowStart("1");
        orderStmtByPortRequest.setFundCode("EEEE");

        StatementResponse statementResponse = null;
        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleResponse> oneServiceResponseBody = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleResponse);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            serviceResponseStmt.setData(statementResponse);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccountDetailService(createHeader(correlationId), fundAccountRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(correlationId), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));
            when(investmentRequestClient.callInvestmentStatementByPortService(createHeader(correlationId), orderStmtByPortRequest)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(correlationId, fundAccountRequest);
        Assert.assertNull(result);
        UtilMap utilMap = new UtilMap();
        FundAccountDetail fundAccountDetailResponse = utilMap.mappingResponse(accountDetailResponse, fundRuleResponse, statementResponse);
        Assert.assertNotNull(fundAccountDetailResponse);
    }

    @Test
    public void testGetFundAccountDetailServiceNull() throws TMBCommonException {
        initAccountDetailResponse();
        initFundRuleResponse();
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleResponse> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleResponse);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(productsExpService.getFundAccountDetail(correlationId, fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(correlationId, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void testGetFundPrePaymentDetail() throws Exception {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        List<String> eligibleAcc = Arrays.asList("200",
                "205",
                "212",
                "212",
                "219",
                "221",
                "225",
                "207",
                "208",
                "251",
                "252",
                "253",
                "255",
                "101",
                "107",
                "108",
                "109",
                "151",
                "152",
                "153",
                "154",
                "155",
                "171",
                "172",
                "173");

        String responseCustomerExp;
        FundHolidayBody fundHolidayBody;
        FundRuleResponse fundRuleResponse;
        CommonData commonData = new CommonData();
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            commonData.setEligibleAccountCodeBuy(eligibleAcc);
            commonDataList.add(commonData);

            when(productExpAsyncService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleResponse));
            when(productExpAsyncService.fetchFundHoliday(any(), anyString())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
            when(productExpAsyncService.fetchCustomerExp(any(), any())).thenReturn(CompletableFuture.completedFuture(responseCustomerExp));
            when(productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(commonDataList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UtilMap utilMap = new UtilMap();
        CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(any(), any());
        CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsyncService.fetchFundHoliday(any(), anyString());
        CompletableFuture<String> fetchCustomerExp = productExpAsyncService.fetchCustomerExp(any(), anyString());
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString());

        CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
        FundRuleResponse fundRuleResponseCom = fetchFundRule.get();
        FundHolidayBody fundHolidayBodyCom = fetchFundHoliday.get();
        String customerExp = fetchCustomerExp.get();

        List<CommonData> commonDataListCom = fetchCommonConfigByModule.get();
        Assert.assertNotNull(customerExp);

        FundPaymentDetailResponse response = utilMap.mappingPaymentResponse(fundRuleResponseCom, fundHolidayBodyCom, commonDataListCom, customerExp);
        Assert.assertNotNull(response);

        TmbOneServiceResponse<FundPaymentDetailResponse> serviceRes = productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void should_return_error_dormant_when_call_getFundPrePaymentDetail_given_correlationId_crmId_payment_detail_request() throws Exception {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        List<String> eligibleAcc = Arrays.asList("200",
                "205",
                "212",
                "212",
                "219",
                "221",
                "225",
                "207",
                "208",
                "251",
                "252",
                "253",
                "255",
                "101",
                "107",
                "108",
                "109",
                "151",
                "152",
                "153",
                "154",
                "155",
                "171",
                "172",
                "173");

        String responseCustomerExp;
        FundHolidayBody fundHolidayBody;
        FundRuleResponse fundRuleResponse;
        CommonData commonData = new CommonData();
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/account_dormant.json")), StandardCharsets.UTF_8);

            commonData.setEligibleAccountCodeBuy(eligibleAcc);
            commonDataList.add(commonData);

            when(productExpAsyncService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleResponse));
            when(productExpAsyncService.fetchFundHoliday(any(), anyString())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
            when(productExpAsyncService.fetchCustomerExp(any(), any())).thenReturn(CompletableFuture.completedFuture(responseCustomerExp));
            when(productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(commonDataList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UtilMap utilMap = new UtilMap();
        CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(any(), any());
        CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsyncService.fetchFundHoliday(any(), anyString());
        CompletableFuture<String> fetchCustomerExp = productExpAsyncService.fetchCustomerExp(any(), anyString());
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString());

        CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
        FundRuleResponse fundRuleResponseCom = fetchFundRule.get();
        FundHolidayBody fundHolidayBodyCom = fetchFundHoliday.get();
        String customerExp = fetchCustomerExp.get();

        List<CommonData> commonDataListCom = fetchCommonConfigByModule.get();
        Assert.assertNotNull(customerExp);

        FundPaymentDetailResponse response = utilMap.mappingPaymentResponse(fundRuleResponseCom, fundHolidayBodyCom, commonDataListCom, customerExp);
        Assert.assertNotNull(response);

        TmbOneServiceResponse<FundPaymentDetailResponse> actual = productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest);
        Assert.assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(),actual.getStatus().getCode());
    }

    @Test
    public void testGetFundPrePaymentDetailNotFound() throws Exception {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        try {
            when(productExpAsyncService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsyncService.fetchFundHoliday(any(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsyncService.fetchCustomerExp(any(), any())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(any(), any());
        CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsyncService.fetchFundHoliday(any(), anyString());
        CompletableFuture<String> fetchCustomerExp = productExpAsyncService.fetchCustomerExp(any(), anyString());
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString());

        CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
        FundRuleResponse fundRuleResponseCom = fetchFundRule.get();
        FundHolidayBody fundHolidayBodyCom = fetchFundHoliday.get();
        String customerExp = fetchCustomerExp.get();
        List<CommonData> commonDataListCom = fetchCommonConfigByModule.get();

        UtilMap utilMap = new UtilMap();
        FundPaymentDetailResponse response = utilMap.mappingPaymentResponse(fundRuleResponseCom, fundHolidayBodyCom, commonDataListCom, customerExp);
        Assert.assertNull(response);
    }

    @Test
    public void testGetFundPrePaymentDetailNotFoundException() {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        try {
            when(accountRequestClient.getAccountList(any(), anyString())).thenThrow(MockitoException.class);
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(MockitoException.class);
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        TmbOneServiceResponse<FundPaymentDetailResponse> serviceRes = productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest);
        Assert.assertNull(serviceRes);
    }

    @Test
    public void testGetFundAccountDetailException() throws TMBCommonException {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        try {
            when(productExpAsyncService.fetchFundAccountDetail(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(correlationId, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void getFundSummaryException() {
        try {
            when(customerService.getAccountSaving(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody getFundSummary = productsExpService.getFundSummary(correlationId, crmId);
        Assert.assertNull(getFundSummary);
    }

    @Test
    public void convertAccountType() {
        String accType = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_SDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_SAVING, accType);
        String accTypeTw = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_DDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_CURRENT, accTypeTw);
    }

    @Test
    public void testisCASADormant() {
        String responseCustomerExp = null;

        try {
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertNotNull(responseCustomerExp);
    }

    @Test
    public void getFundList() throws Exception {
        List<FundClassListInfo> fundAccountRs = new ArrayList<>();
        FundClassListInfo fundAccount;
        FundSummaryBody fundHolidayBody;
        List<CustomerFavoriteFundData> favoriteFundData = new ArrayList<>();
        CustomerFavoriteFundData favoriteFundData1 = new CustomerFavoriteFundData();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundAccount = mapper.readValue(Paths.get("src/test/resources/investment/fund_list.json").toFile(), FundClassListInfo.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(), FundSummaryBody.class);

            favoriteFundData1.setFundCode("AAAA");
            favoriteFundData1.setIsFavorite("N");
            favoriteFundData1.setId("1");
            favoriteFundData1.setCustomerId("100000023333");

            fundAccountRs.add(fundAccount);
            favoriteFundData.add(favoriteFundData1);

            when(productExpAsyncService.fetchFundListInfo(any(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(fundAccountRs));
            when(productExpAsyncService.fetchFundSummary(any(), any())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
            when(productExpAsyncService.fetchFundFavorite(any(), anyString())).thenReturn(CompletableFuture.completedFuture(favoriteFundData));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<FundClassListInfo> listFund;
        CompletableFuture<List<FundClassListInfo>> fetchFundListInfo = productExpAsyncService.fetchFundListInfo(any(), anyString(), anyString());
        CompletableFuture<FundSummaryBody> fetchFundSummary = productExpAsyncService.fetchFundSummary(any(), any());
        CompletableFuture<List<CustomerFavoriteFundData>> fetchFundFavorite = productExpAsyncService.fetchFundFavorite(any(), anyString());
        CompletableFuture.allOf(fetchFundListInfo, fetchFundSummary, fetchFundFavorite);

        listFund = fetchFundListInfo.get();
        FundSummaryBody fundSummaryResponse = fetchFundSummary.get();
        List<CustomerFavoriteFundData> customerFavoriteFundDataList = fetchFundFavorite.get();
        listFund = UtilMap.mappingFollowingFlag(listFund, customerFavoriteFundDataList);
        listFund = UtilMap.mappingBoughtFlag(listFund, fundSummaryResponse);

        CacheModel cacheModel = UtilMap.mappingCache("teeeeeeee", "abc");
        Assert.assertNotNull(cacheModel);

        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        Assert.assertNotNull(listFund);
        List<FundClassListInfo> result = productsExpService.getFundList(correlationId, crmId, fundListRequest);
        Assert.assertNotNull(result);
    }

    @Test
    public void getFundListWithException() {
        try {
            when(productExpAsyncService.fetchFundListInfo(any(), anyString(), anyString())).thenReturn(null);
            when(productExpAsyncService.fetchFundSummary(any(), any())).thenReturn(null);
            when(productExpAsyncService.fetchFundFavorite(any(), anyString())).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        List<FundClassListInfo> result = productsExpService.getFundList(correlationId, crmId, fundListRequest);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_return_suggest_allocation_dto_when_get_suggest_allocation_given_correlationId_and_crmId() throws Exception {
        String crmId = "00000018592884";
        ObjectMapper mapper = new ObjectMapper();
        String portListReturn = "{\"status\":{\"code\":\"0000\",\"message\":\"success\",\"service\":\"accounts-service\",\"description\":\"success\"},\"data\":{\"saving_accounts\":[{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416367\",\"account_name\":\"MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0225\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":1.0335840775E9,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416458\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0221\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":922963.66,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416524\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0211\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":5000.0,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0300\",\"acct_nbr\":\"00001103318497\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"CDA\",\"product_code\":\"0664\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":10000.0,\"balance_currency\":\"THB\"}],\"current_accounts\":[],\"loan_accounts\":[],\"trade_finance_accounts\":[],\"treasury_accounts\":[],\"debit_card_accounts\":[],\"merchant_accounts\":[],\"foreign_exchange_accounts\":[],\"mutual_fund_accounts\":[{\"appl_code\":\"97\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0000\",\"acct_ctrl3\":\"0000\",\"acct_ctrl4\":\"0000\",\"acct_nbr\":\"PT000000000001829798\",\"product_group_code\":\"MF\",\"product_group_code_ec\":\"0000\",\"product_code\":\"\",\"relationship_code\":\"PRIIND\",\"xps_account_status\":\"BLANK\"},{\"appl_code\":\"97\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0000\",\"acct_ctrl3\":\"0000\",\"acct_ctrl4\":\"0000\",\"acct_nbr\":\"PT000000000001829800\",\"product_group_code\":\"MF\",\"product_group_code_ec\":\"0000\",\"product_code\":\"\",\"relationship_code\":\"PRIIND\",\"xps_account_status\":\"BLANK\"}],\"bancassurance_accounts\":[],\"other_accounts\":[]}}";
        when(accountRequestClient.getPortList(any(), anyString())).thenReturn(portListReturn);
        FundSummaryBody fundSummaryBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/invest_fundsummary_for_suggestallocation_data.json").toFile(), FundSummaryBody.class);
        when(productExpAsyncService.fetchFundSummary(any(), any())).thenReturn(CompletableFuture.completedFuture(fundSummaryBody));
        when(productExpAsyncService.fetchSuitabilityInquiry(any(), anyString())).thenReturn(CompletableFuture.completedFuture(SuitabilityInfo.builder().suitabilityScore("2").build()));
        FundAllocationResponse fundAllocationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation.json").toFile(), FundAllocationResponse.class);
        TmbOneServiceResponse<FundAllocationResponse> response = new TmbOneServiceResponse<>();
        response.setData(fundAllocationResponse);
        when(investmentRequestClient.callInvestmentFundAllocation(any(), any())).thenReturn(ResponseEntity.ok(response));
        SuggestAllocationDTO suggestAllocationDTOMock = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation_dto.json").toFile(), SuggestAllocationDTO.class);
        SuggestAllocationDTO suggestAllocationDTO = productsExpService.getSuggestAllocation(correlationId, crmId);
        Assert.assertNotNull(suggestAllocationDTO);
        Assert.assertEquals(suggestAllocationDTOMock, suggestAllocationDTO);
    }

    @Test
    public void should_return_null_when_get_suggest_allocation_given_correlationId_and_crmId() {
        String crmId = "00000018592884";
        when(accountRequestClient.getPortList(any(), anyString())).thenThrow(RuntimeException.class);
        SuggestAllocationDTO suggestAllocationDTO = productsExpService.getSuggestAllocation(correlationId, crmId);
        Assert.assertNull(suggestAllocationDTO);
    }
}