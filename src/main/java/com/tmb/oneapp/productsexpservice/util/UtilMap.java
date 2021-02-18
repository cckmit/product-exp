package com.tmb.oneapp.productsexpservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.*;

public class UtilMap {
    private static TMBLogger<UtilMap> logger = new TMBLogger<>(UtilMap.class);

    /**
     * Generic Method to mappingResponse
     *
     * @param response
     * @param responseEntity
     * @return FundAccountRs
     */
    public FundAccountRs validateTMBResponse(ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response,
                            ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity){
            if(StringUtils.isEmpty(response) && StringUtils.isEmpty(responseEntity)
            && HttpStatus.OK.value() != response.getStatusCode().value()
            && HttpStatus.OK.value() != responseEntity.getStatusCode().value()){
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
        BeanUtils.copyProperties(accDetailBody.getDetailFund(), accountDetail);
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

    /**
     * Generic Method to mappingResponse
     *
     * @param responseEntity
     * @param responseFundHoliday
     * @return FundPaymentDetailRs
     */
    public FundPaymentDetailRs mappingPaymentResponse(ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity,
                                               ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> responseFundHoliday,
                                               String responseCustomerExp){
        if(StringUtils.isEmpty(responseEntity)
                || StringUtils.isEmpty(responseFundHoliday)
                || HttpStatus.OK != responseEntity.getStatusCode()
                || HttpStatus.OK != responseFundHoliday.getStatusCode()
                || StringUtils.isEmpty(responseCustomerExp)){
            return null;
        }else{
            FundPaymentDetailRs fundPaymentDetailRs = new FundPaymentDetailRs();
            FundHolidayClassList fundHolidayUnit = null;
            List<FundHolidayClassList> fundHolidayClassList = new ArrayList<>();
            List<FundHolidayClassList> fundHolidayClassListRs = responseFundHoliday.getBody().getData().getFundClassList();
            for(FundHolidayClassList fundHoliday : fundHolidayClassListRs){
                fundHolidayUnit = new FundHolidayClassList();
                fundHolidayUnit.setFundCode(fundHoliday.getFundCode());
                fundHolidayUnit.setFundHouseCode(fundHoliday.getFundHouseCode());
                fundHolidayUnit.setHolidayDate(fundHoliday.getHolidayDate());
                fundHolidayUnit.setHolidayDesc(fundHoliday.getHolidayDesc());
                fundHolidayClassList.add(fundHolidayUnit);
            }
            fundPaymentDetailRs.setFundHolidayList(fundHolidayClassList);

            FundRule fundRule = new FundRule();
            List<FundRuleInfoList> fundRuleInfoList = responseEntity.getBody().getData().getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundPaymentDetailRs.setFundRule(fundRule);

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = null;
                node = mapper.readValue(responseCustomerExp, JsonNode.class);
                ArrayNode arrayNode = (ArrayNode) node.get("data");
                int size = arrayNode.size();
                DepositAccount depositAccount = null;
                List<DepositAccount> depositAccountList = new ArrayList<>();
                if(size > 0) {
                    for (int i = 0; i < size; i++) {
                        JsonNode itr = arrayNode.get(i);
                        depositAccount = new DepositAccount();
                        depositAccount.setAccountNumber(itr.get("account_number_display").textValue());
                        depositAccount.setAccountStatus(itr.get("account_status_text").textValue());
                        String productGroupCode = itr.get("product_group_code").textValue();
                        String accType = "";
                        if(ProductsExpServiceConstant.ACC_TYPE_SDA.equals(productGroupCode)){
                            accType = ProductsExpServiceConstant.ACC_TYPE_SAVING;
                        }else if(ProductsExpServiceConstant.ACC_TYPE_DDA.equals(productGroupCode)){
                            accType = ProductsExpServiceConstant.ACC_TYPE_CURRENT;
                        }
                        depositAccount.setAccountType(accType);
                        depositAccount.setProductNameEN(itr.get("product_name_Eng").textValue());
                        depositAccount.setProductNameTH(itr.get("product_name_TH").textValue());
                        depositAccount.setAvailableBalance(new BigDecimal(itr.get("current_balance").textValue()));
                        depositAccountList.add(depositAccount);
                    }
                }
                fundPaymentDetailRs.setDepositAccountList(depositAccountList);
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            }

            return fundPaymentDetailRs;
        }
    }
}
