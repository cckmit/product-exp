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
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundContent;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListClass;
import org.springframework.http.MediaType;
import java.text.SimpleDateFormat;

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
        if((StringUtils.isEmpty(response) && StringUtils.isEmpty(responseEntity))
                || (HttpStatus.OK != response.getStatusCode() && HttpStatus.OK != responseEntity.getStatusCode())){
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
                || HttpStatus.OK != responseEntity.getStatusCode()
                || StringUtils.isEmpty(responseCustomerExp)){
            return null;
        }else{
            FundPaymentDetailRs fundPaymentDetailRs = new FundPaymentDetailRs();
            if(!StringUtils.isEmpty(responseFundHoliday) && HttpStatus.OK == responseFundHoliday.getStatusCode()) {
                FundHolidayClassList fundHolidayUnit = null;
                List<FundHolidayClassList> fundHolidayClassList = new ArrayList<>();
                List<FundHolidayClassList> fundHolidayClassListRs = responseFundHoliday.getBody().getData().getFundClassList();
                for (FundHolidayClassList fundHoliday : fundHolidayClassListRs) {
                    fundHolidayUnit = new FundHolidayClassList();
                    fundHolidayUnit.setFundCode(fundHoliday.getFundCode());
                    fundHolidayUnit.setFundHouseCode(fundHoliday.getFundHouseCode());
                    fundHolidayUnit.setHolidayDate(fundHoliday.getHolidayDate());
                    fundHolidayUnit.setHolidayDesc(fundHoliday.getHolidayDesc());
                    fundHolidayClassList.add(fundHolidayUnit);
                }
                fundPaymentDetailRs.setFundHolidayList(fundHolidayClassList);
            }

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
                for (int i = 0; i < size; i++) {
                        JsonNode itr = arrayNode.get(i);
                        depositAccount = new DepositAccount();
                        depositAccount.setAccountNumber(itr.get("account_number_display").textValue());
                        depositAccount.setAccountStatus(itr.get("account_status_text").textValue());
                        String accType = itr.get("product_group_code").textValue();
                        depositAccount.setAccountType(convertAccountType(accType));
                        depositAccount.setAccountTypeShort(accType);
                        depositAccount.setProductNameEN(itr.get("product_name_Eng").textValue());
                        depositAccount.setProductNameTH(itr.get("product_name_TH").textValue());
                        depositAccount.setAvailableBalance(new BigDecimal(itr.get("current_balance").textValue()));
                        depositAccountList.add(depositAccount);
                }
                fundPaymentDetailRs.setDepositAccountList(depositAccountList);
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            }

            return fundPaymentDetailRs;
        }
    }

    /**
     * Generic Method to convert Account Type form 3 digits to 1 digit
     *
     *
     * @param productType
     * @return String Account Type
     */
    public static String convertAccountType(String productType){
        String accType = "";
        switch (productType){
            case ProductsExpServiceConstant.ACC_TYPE_SDA :
                accType = ProductsExpServiceConstant.ACC_TYPE_SAVING;
                break;
            case ProductsExpServiceConstant.ACC_TYPE_DDA :
                accType = ProductsExpServiceConstant.ACC_TYPE_CURRENT;
                break;
            default: accType = "";
        }
        return accType;
    }

    /**
     * Generic Method to Get Current Date with Format
     *
     * @param startTime the start HHMM
     * @param endTime the end HHMM
     * @return boolean
     */
    public static boolean isBusinessClose(String startTime, String endTime){
        boolean isClose = true;
        try {
            if(!StringUtils.isEmpty(startTime)
                    && !StringUtils.isEmpty(endTime)) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(ProductsExpServiceConstant.MF_TIME_HHMM);
                String getCurrentTime = sdf.format(cal.getTime());
                if((getCurrentTime.compareTo(startTime) > 0) && (getCurrentTime.compareTo(endTime) > 0)){
                    return isClose;
                }
            }
        }catch (Exception e){
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
        }
        return false;
    }

    /**
     * Generic Method to create HTTP Header
     *
     * @param correlationId
     * @return
     */
    public static Map<String, Object> createHeader(String correlationId, int pageSize, int pageNo) {
        Map<String, Object> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        invHeaderReqParameter.put(ProductsExpServiceConstant.PAGE_SIZE, pageSize);
        invHeaderReqParameter.put(ProductsExpServiceConstant.PAGE_NO, pageNo);
        return invHeaderReqParameter;
    }

    /**
     * Generic Method to create HTTP Header
     *
     * @param correlationId
     * @return
     */
    public static Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    /**
     * Generic Method to create HTTP Header
     *
     * @param ffsRequestBody
     * @param fundListClass
     * @return
     */
    public static boolean isOfShelfCheck(FfsRequestBody ffsRequestBody, FundListClass fundListClass) {
        boolean isFundOfShelf = true;
        if(!StringUtils.isEmpty(fundListClass)) {
            for (FundContent contents : fundListClass.getContent()) {
                String fundCode = contents.getFundCode();
                if(fundCode.equals(ffsRequestBody.getFundCode())){
                    isFundOfShelf = false;
                    break;
                }
            }
        }
        return isFundOfShelf;
    }

    /**
     * Generic Method to mappingResponse
     *
     * @param responseCustomerExp
     * @return boolean
     */
    public static boolean isCASADormant(String responseCustomerExp){
        if(StringUtils.isEmpty(responseCustomerExp)){
            return true;
        }else{
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = null;
                node = mapper.readValue(responseCustomerExp, JsonNode.class);
                ArrayNode arrayNode = (ArrayNode) node.get("data");
                int size = arrayNode.size();
                List<Integer> countDormant = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JsonNode itr = arrayNode.get(i);
                    String accStatus = itr.get("account_status_text").textValue();
                    BigDecimal balance = new BigDecimal(itr.get("current_balance").textValue());
                    BigDecimal zeroBalance = new BigDecimal("0");
                    switch (accStatus) {
                        case ProductsExpServiceConstant.ACTIVE_STATUS :
                        case ProductsExpServiceConstant.INACTIVE_STATUS :
                            if((balance.compareTo(zeroBalance) == 0)) countDormant.add(i);
                            break;
                        case ProductsExpServiceConstant.DORMANT_STATUS :
                            countDormant.add(i);
                            break;
                        default: break;
                    }
                }
                return (size == countDormant.size());
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            }
            return false;
        }
    }


}
