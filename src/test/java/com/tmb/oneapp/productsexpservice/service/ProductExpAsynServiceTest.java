package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ProductExpAsynServiceTest {
     InvestmentRequestClient investmentRequestClient;
     AccountRequestClient accountRequestClient;
     CustomerServiceClient customerServiceClient;
     CommonServiceClient commonServiceClient;
     ProductExpAsynService productExpAsynService;
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;

    @BeforeEach
    public void setUp() {

        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        commonServiceClient = mock(CommonServiceClient.class);
        customerServiceClient = mock(CustomerServiceClient.class);
        productExpAsynService = new ProductExpAsynService(investmentRequestClient, accountRequestClient, customerServiceClient, commonServiceClient);
    }

    @Test
    public void testGetFundAccdetail() throws Exception {
        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<AccDetailBody> response = productExpAsynService.fetchFundAccDetail(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void testGetFundAccdetailException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundAccDetailService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<AccDetailBody> response = productExpAsynService.fetchFundAccDetail(any(), any());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testGetFundRule() throws Exception {

        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<FundRuleBody> response = productExpAsynService.fetchFundRule(any(), any());
        Assert.assertNotNull(response);

    }

    @Test
    public void testGetFundRuleException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundRuleBody> response = productExpAsynService.fetchFundRule(any(), any());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testGetStmtByPort() throws Exception {
        try{
            StatementResponse statementResponse = null;
            TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            serviceResponseStmt.setData(statementResponse);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentStmtByPortService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        CompletableFuture<StatementResponse> response = productExpAsynService.fetchStmtByPort(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void testGetStmtByPortException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentStmtByPortService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<StatementResponse> response = productExpAsynService.fetchStmtByPort(any(), any());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testGetFundHoliday() throws Exception {
        try{
            FundHolidayBody fundHolidayBody = null;
            TmbOneServiceResponse<FundHolidayBody> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            serviceResponseStmt.setData(fundHolidayBody);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        CompletableFuture<FundHolidayBody> response = productExpAsynService.fetchFundHoliday(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void testGetFundHolidayException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundHolidayBody> response = productExpAsynService.fetchFundHoliday(any(), any());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void testGetCustomer() throws Exception {
        try{
            String responseCustomerExp = null;
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        CompletableFuture<String> response = productExpAsynService.fetchCustomerExp(any(), any());
        Assert.assertNotNull(response);
    }



}
