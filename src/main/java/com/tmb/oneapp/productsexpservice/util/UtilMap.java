package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class UtilMap {

    /**
     * Generic Method to mappingResponse
     *
     * @param response
     * @param responseEntity
     * @return FundAccountRs
     */
    public FundAccountRs ValidateResponse(ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response,
                            ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity){
            if(StringUtils.isEmpty(response)){
                return null;
            }else if(StringUtils.isEmpty(responseEntity)){
                return null;
            } else{

                FundAccountRs fundAccountRs = new FundAccountRs();
                UtilMap utilMap = new UtilMap();
                FundAccountDetail fundAccountDetail = utilMap.mappingResponse(response.getBody().getData(),
                        responseEntity.getBody().getData());
                fundAccountRs.setDetails(fundAccountDetail);
                return fundAccountRs;
            }
    }

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
