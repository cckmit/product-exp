package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ProductsExpService class will get fund Details from MF Service
 *
 *
 */
@Service
public class ProductsExpService {
    private static TMBLogger<ProductsExpService> logger = new TMBLogger<>(ProductsExpService.class);
    private InvestmentRequestClient investmentRequestClient;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient) {
        this.investmentRequestClient = investmentRequestClient;
    }


    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundAccountRq
     * @param correlationId
     * @return
     */
    @LogAround
    public FundAccountRs getFundAccountDetail(String correlationId, FundAccountRq fundAccountRq){
        FundAccountRs fundAccountRs = null;
        FundAccountRequestBody fundAccountRequestBody = new FundAccountRequestBody();
        fundAccountRequestBody.setFundCode(ProductsExpServiceConstant.FUND_CODE_ACCDETAIL);
        fundAccountRequestBody.setServiceType(fundAccountRq.getServiceType());
        fundAccountRequestBody.setUnitHolderNo(fundAccountRq.getUnitHolderNo());

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ProductsExpServiceConstant.FUND_CODE_RULE);
        fundRuleRequestBody.setFundHouseCode(ProductsExpServiceConstant.FUND_HOUSE_CODE_RULE);
        fundRuleRequestBody.setTranType(fundAccountRq.getTranType());

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response = null;
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = null;
        try {
            response = investmentRequestClient.callInvestmentFundAccDetailService(invHeaderReqParameter, fundAccountRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, response);
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
        }catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return fundAccountRs;
        }
        if(!StringUtils.isEmpty(response) && !StringUtils.isEmpty(responseEntity)
            && HttpStatus.OK == response.getStatusCode()
            && HttpStatus.OK == responseEntity.getStatusCode()){
                fundAccountRs = new FundAccountRs();
                FundAccountDetail fundAccountDetail = mappingResponse(response.getBody().getData(),
                        responseEntity.getBody().getData());
                fundAccountRs.setDetails(fundAccountDetail);

                return fundAccountRs;
        }
        return fundAccountRs;
    }


    /**
     * Generic Method to create HTTP Header
     *
     * @param correlationId
     * @return
     */
    private Map<String, String> createHeader(String correlationId){
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }


    /**
     * Generic Method to mappingResponse
     *
     * @param accDetailBody
     * @param fundRuleBody
     * @return FundAccountDetail
     */
    private FundAccountDetail mappingResponse(AccDetailBody accDetailBody, FundRuleBody fundRuleBody){
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
