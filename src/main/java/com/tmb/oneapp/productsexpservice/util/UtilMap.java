package com.tmb.oneapp.productsexpservice.util;

import com.tmb.oneapp.productsexpservice.model.response.accdetail.AccountDetail;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundOrderHistory;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundRule;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class UtilMap {

    /**
     * Generic Method to mappingResponse
     *
     * @param accDetailBody
     * @param fundRuleBody
     * @return FundAccountDetail
     */
    public FundAccountDetail mappingResponse(AccDetailBody accDetailBody, FundRuleBody fundRuleBody){
        FundRule fundRule = new FundRule();

        List<FundRuleInfoList> fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
        FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
        BeanUtils.copyProperties(ruleInfoList, fundRule);
        fundRule.setIpoflag(ruleInfoList.getIpoflag());

        AccountDetail accountDetail = new AccountDetail();
        BeanUtils.copyProperties(accountDetail, accDetailBody.getDetailFund());
        List<Order> orders = accDetailBody.getOrderToBeProcess().getOrder();
        List<FundOrderHistory> ordersHistories = new ArrayList<>();

        for(Order order : orders){
            FundOrderHistory fundOrderHistory = new FundOrderHistory();
            BeanUtils.copyProperties(order, fundOrderHistory);
            ordersHistories.add(fundOrderHistory);
        }
        accountDetail.setOrdersHistories(ordersHistories);
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        fundAccountDetail.setFundRule(fundRule);
        fundAccountDetail.setAccountDetail(accountDetail);

        return fundAccountDetail;
    }
}
