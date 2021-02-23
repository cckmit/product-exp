package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import com.tmb.oneapp.productsexpservice.model.response.investment.OrderToBeProcess;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

public class ProductExpServiceControllerTest {
    @Mock
    ProductsExpService productsExpService;
    @InjectMocks
    ProductExpServiceController productExpServiceController;

    private final String success_code = "0000";
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private FundAccountRs fundAccountRs = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private FundAccountDetail mappingResponse(AccDetailBody accDetailBody, FundRuleBody fundRuleBody){
        FundRule fundRule = new FundRule();
        List<FundRuleInfoList> fundRuleInfoList = null;
        AccountDetail accountDetail = new AccountDetail();
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        if(!StringUtils.isEmpty(fundRuleBody)) {
            fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundRule.setIpoflag(ruleInfoList.getIpoflag());
            BeanUtils.copyProperties(accountDetail, accDetailBody.getDetailFund());
            List<Order> orders = accDetailBody.getOrderToBeProcess().getOrder();
            List<FundOrderHistory> ordersHistories = new ArrayList<>();

            for (Order order : orders) {
                FundOrderHistory fundOrderHistory = new FundOrderHistory();
                BeanUtils.copyProperties(order, fundOrderHistory);
                ordersHistories.add(fundOrderHistory);
            }
            accountDetail.setOrdersHistories(ordersHistories);
            fundAccountDetail.setFundRule(fundRule);
            fundAccountDetail.setAccountDetail(accountDetail);
        }

        return fundAccountDetail;
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

    private void initSuccessResponseAccDetail(){
        fundAccountRs = new FundAccountRs();
        FundAccountDetail details = new FundAccountDetail();
        FundRule fundRule = new FundRule();
        fundRule.setIpoflag("N");
        fundRule.setFundAllowOtx("Y");
        fundRule.setFundCode("TESEQDSSFX");
        fundRule.setRiskRate("1");
        fundRule.setFundHouseCode("TFUND");
        fundRule.setOrderType("1");
        fundRule.setAllotType("3");
        fundRule.setAllowAipFlag("Y");
        fundRule.setDateAfterIpo("20200413");
        fundRule.setFrontEndFee("0");
        details.setFundRule(fundRule);

        AccountDetail accountDetail = new AccountDetail();
        List<FundOrderHistory> ordersHistories = new ArrayList<>();
        FundOrderHistory fundOrderHistory = new FundOrderHistory();
        fundOrderHistory.setAmount("2000.00");
        fundOrderHistory.setOrderDate("20200413");
        fundOrderHistory.setChannelHubEN("EN");
        fundOrderHistory.setOrderReference("EEEE");
        fundOrderHistory.setOrderDateTemp("20200413");
        fundOrderHistory.setEfftDate("20200413");
        fundOrderHistory.setItemNo("1");
        fundOrderHistory.setStatusHubEN("SS");
        fundOrderHistory.setTranTypeHubEN("1");
        fundOrderHistory.setTranTypeHubTH("1");

        FundOrderHistory fundOrderHistoryOne = new FundOrderHistory();
        fundOrderHistoryOne.setAmount("2000.00");
        fundOrderHistoryOne.setOrderDate("20200413");
        fundOrderHistoryOne.setChannelHubEN("EN");
        fundOrderHistoryOne.setOrderReference("EEEE");
        fundOrderHistoryOne.setOrderDateTemp("20200413");
        fundOrderHistoryOne.setEfftDate("20200413");
        fundOrderHistoryOne.setItemNo("2");
        fundOrderHistoryOne.setStatusHubEN("SS");
        fundOrderHistoryOne.setTranTypeHubEN("1");
        fundOrderHistoryOne.setTranTypeHubTH("1");

        ordersHistories.add(fundOrderHistory);
        ordersHistories.add(fundOrderHistoryOne);
        accountDetail.setOrdersHistories(ordersHistories);

        details.setAccountDetail(accountDetail);
        fundAccountRs.setDetails(details);

    }

    @Test
    public void testgetFundAccountDetailFullReturn() throws Exception {
        initSuccessResponseAccDetail();
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");
        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountRs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        Assert.assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertEquals(success_code, actualResult.getBody().getStatus().getCode());

        FundAccountRs newResponse = actualResult.getBody().getData();
        Assert.assertEquals(fundAccountRs, newResponse);
        Assert.assertEquals(fundAccountRs.getDetails().getAccountDetail().getOrdersHistories().size(),
                newResponse.getDetails().getAccountDetail().getOrdersHistories().size());

    }

    @Test
    public void testgetFundAccountDetailNotfound() throws Exception {
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");
        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        Assert.assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void testgetFundAccountDetail() throws Exception {
        initAccDetailBody();
        initFundRuleBody();

        FundAccountRs fundAccountRs = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        AccDetailBody accDetailBody = null;
        FundRuleBody fundRuleBody = null;
        try {
            fundAccountRs = new FundAccountRs();

            FundAccountDetail fundAccountDetail = mappingResponse(accDetailBody, fundRuleBody);
            fundAccountRs.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountRs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        Assert.assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getDetails());
    }


    @Test
    public void testgetFundPrePaymentDetail() throws Exception {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        FundPaymentDetailRs fundPaymentDetailRs = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            fundPaymentDetailRs = mapper.readValue(Paths.get("src/test/resources/investment/fund_payment_detail.json").toFile(), FundPaymentDetailRs.class);
            when(productsExpService.getFundPrePaymentDetail(corrID, fundPaymentDetailRq)).thenReturn(fundPaymentDetailRs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> actualResult = productExpServiceController
                .getFundPrePaymentDetail(corrID, fundPaymentDetailRq);
        Assert.assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getFundRule());
        Assert.assertNotNull(actualResult.getBody().getData().getDepositAccountList());
        Assert.assertNotNull(actualResult.getBody().getData().getFundHolidayList());
    }

    @Test
    public void testinsertActivityLog() throws Exception {
        ActivityLogs activityLogs = null;
        activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(corrID,
                ProductsExpServiceConstant.SUCCESS_MESSAGE, ProductsExpServiceConstant.ACTIVITY_LOG_SUCCESS,
                "0000000012345666", ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING,
                "N");
        Assert.assertNull(activityLogs);
    }


    @Test
    public void getFundFFSAndValidation() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        FfsRsAndValidation fundRsAndValidation = null;
        try {
            fundRsAndValidation = new FfsRsAndValidation();
            FfsData body = new FfsData();
            body.setFactSheetData("fdg;klghbdf;jbneoa;khnd'flbkndflkhnreoid;bndzfklbnoresibndlzfk[bnseriohnbodkzfvndsogb");
            fundRsAndValidation.setBody(body);
            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }


    @Test
    public void getFundFFSAndValidationFail() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        FfsRsAndValidation fundRsAndValidation = null;
        try {
            fundRsAndValidation = new FfsRsAndValidation();
            fundRsAndValidation.setServiceClose(true);
            fundRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            fundRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_MESSAGE);
            fundRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_DESC);

            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }


}

