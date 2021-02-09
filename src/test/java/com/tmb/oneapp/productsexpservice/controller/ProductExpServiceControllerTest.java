package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investmentrs.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investmentrs.DetailFund;
import com.tmb.oneapp.productsexpservice.model.response.investmentrs.Order;
import com.tmb.oneapp.productsexpservice.model.response.investmentrs.OrderToBeProcess;
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
    }


}
