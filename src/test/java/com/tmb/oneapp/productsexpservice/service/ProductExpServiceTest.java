package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import com.tmb.oneapp.productsexpservice.model.response.investment.OrderToBeProcess;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

public class ProductExpServiceTest {

    InvestmentRequestClient investmentRequestClient;
    ProductsExpService productsExpService;

    private final String success_code = "0000";
    private final String notfund_code = "0009";
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        productsExpService = new ProductsExpService(investmentRequestClient);
    }


    private void initAccDetailBody(){
        accDetailBody = new AccDetailBody();
        DetailFund detailFund = new DetailFund();
        detailFund.setFundHouseCode("TTTTT");
        detailFund.setFundHouseCode("EEEEE");
        accDetailBody.setDetailFund(detailFund);

        OrderToBeProcess orderToBeProcess = new OrderToBeProcess();
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setAmount("200");
        order.setOrderDate("20201212");
        orders.add(order);
        orderToBeProcess.setOrder(orders);
        accDetailBody.setOrderToBeProcess(orderToBeProcess);

    }

    private void initFundRuleBody(){
        fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleBody.setFundRuleInfoList(fundRuleInfoList);
    }

    private Map<String, String> createHeader(String correlationId){
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }


    @Test
    public void testGetFundAccdetailAndFundRule() throws Exception {

        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");


        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("2");
        fundRuleRequestBody.setFundHouseCode("TTTTT");
        fundRuleRequestBody.setFundCode("EEEEE");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseResponseEntity = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);
            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseResponseEntity = investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody);
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertNotNull(responseResponseEntity);
        Assert.assertNotNull(responseEntity);
        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void testGetFundAccdetail() throws Exception {

        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();


        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.OK.value(),responseEntity.getStatusCodeValue());
        Assert.assertEquals("FFFFF",responseEntity.getBody().getData().getDetailFund().getFundHouseCode());
        Assert.assertEquals(2,responseEntity.getBody().getData().getOrderToBeProcess().getOrder()
        .size());
        Assert.assertNotNull(responseEntity.getBody().getData().getDetailFund());
    }


    @Test
    public void testGetFundRule() throws Exception {

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("2");
        fundRuleRequestBody.setFundHouseCode("TTTTT");
        fundRuleRequestBody.setFundCode("EEEEE");

        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRuleResponseEntity = null;

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fundRuleResponseEntity = investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody);
        Assert.assertEquals(HttpStatus.OK,fundRuleResponseEntity.getStatusCode());
        Assert.assertEquals("TESEQDSSFX",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundCode());
        Assert.assertEquals("TFUND",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundHouseCode());
        Assert.assertEquals("20200413",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getTranStartDate());
        Assert.assertEquals("3",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getOrderType());
        Assert.assertEquals("3",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getAllotType());
        Assert.assertEquals("06",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getRiskRate());
    }

    @Test
    public void testGetFundAccdetailNull() throws Exception {
        initAccDetailBody();
        initFundRuleBody();
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000000000138924");
        fundAccountRq.setServiceType("2");
        fundAccountRq.setFundCode("DDD");


        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("1");
        fundRuleRequestBody.setFundHouseCode("TFUND");
        fundRuleRequestBody.setFundCode("TMB50");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }


    @Test
    public void testGetFundAccdetailServiceNull() throws Exception {
        initAccDetailBody();
        initFundRuleBody();
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000000000138924");
        fundAccountRq.setServiceType("2");
        fundAccountRq.setFundCode("DDD");


        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("1");
        fundRuleRequestBody.setFundHouseCode("TFUND");
        fundRuleRequestBody.setFundCode("TMB50");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

           when(productsExpService.getFundAccountDetail(corrID, fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }


}

