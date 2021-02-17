package com.tmb.oneapp.productsexpservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
import java.io.IOException;
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
                || HttpStatus.OK.value() != responseEntity.getStatusCode().value()
                || HttpStatus.OK.value() != responseFundHoliday.getStatusCode().value()
                || StringUtils.isEmpty(responseCustomerExp)){
            return null;
        }else{
            FundPaymentDetailRs fundPaymentDetailRs = new FundPaymentDetailRs();
            FundHolidayClassList fundHolidayUnit = null;
            List<String> mutualFundList = null;
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
                JsonNode dataNode = node.get("data");
                JsonNode mutualFundListRs = dataNode.get("mutual_fund_accounts");
                ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
                });
                mutualFundList = reader.readValue(mutualFundListRs);
                fundPaymentDetailRs.setMutualFundAccountList(mutualFundList);

                ArrayNode arrayNode = (ArrayNode) dataNode.get("deposit_account_lists");
                int size = arrayNode.size();
                DepositAccount depositAccount = null;
                List<DepositAccount> depositAccountList = new ArrayList<>();
                if(size > 0) {
                    for (int i = 0; i < size; i++) {
                        JsonNode itr = arrayNode.get(i);
                        depositAccount = new DepositAccount();
                        depositAccount.setAccountNumber(itr.get("account_number") != null ? itr.get("account_number").textValue() : "");
                        depositAccount.setAccountStatus(itr.get("account_status") != null ? itr.get("account_status").textValue() : "");
                        depositAccount.setAccountType(itr.get("account_type") != null ? itr.get("account_type").textValue() : "");
                        depositAccount.setProductNameEN(itr.get("product_name_en") != null ? itr.get("product_name_en").textValue() : "");
                        depositAccount.setProductNameTH(itr.get("product_name_th") != null ? itr.get("product_name_th").textValue() : "");
                        depositAccount.setAvailableBalance(new BigDecimal(itr.get("available_balance").toString()));
                        depositAccountList.add(depositAccount);
                    }
                }
                fundPaymentDetailRs.setDepositAccountList(depositAccountList);
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            } catch (IOException ex) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            }


            return fundPaymentDetailRs;
        }
    }
}
