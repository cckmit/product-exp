package com.tmb.oneapp.productsexpservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.*;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.*;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementList;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.tmb.oneapp.productsexpservice.util.TimeUtil.isTimeBetweenTwoTime;

public class UtilMap {

    private static final TMBLogger<UtilMap> logger = new TMBLogger<>(UtilMap.class);

    /**
     * Generic Method to validateTMBResponse
     *
     * @param accountDetailBody
     * @param fundRuleBody
     * @param statementResponse
     * @return FundAccountRs
     */
    public static FundAccountResponse validateTMBResponse(AccountDetailBody accountDetailBody,
                                                          FundRuleBody fundRuleBody,
                                                          StatementResponse statementResponse) {

        if ((StringUtils.isEmpty(accountDetailBody) && StringUtils.isEmpty(fundRuleBody))) {
            return null;
        } else {
            FundAccountResponse fundAccountResponse = new FundAccountResponse();
            FundAccountDetail fundAccountDetail = UtilMap.mappingResponse(accountDetailBody, fundRuleBody, statementResponse);
            fundAccountResponse.setDetails(fundAccountDetail);
            return fundAccountResponse;
        }
    }

    /**
     * Generic Method to mappingResponse
     *
     * @param accountDetailBody
     * @param fundRuleBody
     * @return FundAccountDetail
     */
    public static FundAccountDetail mappingResponse(AccountDetailBody accountDetailBody,
                                                    FundRuleBody fundRuleBody,
                                                    StatementResponse statementResponse) {

        AccountDetail accountDetail = new AccountDetail();
        BeanUtils.copyProperties(accountDetailBody.getFundDetail(), accountDetail);
        List<FundOrderHistory> ordersHistories = new ArrayList<>();
        List<StatementList> statementList = statementResponse.getStatementList();
        FundOrderHistory order;
        for (StatementList stmt : statementList) {
            order = new FundOrderHistory();
            BeanUtils.copyProperties(stmt, order);
            ordersHistories.add(order);
        }
        accountDetail.setOrdersHistories(ordersHistories);
        Collections.sort(fundRuleBody.getFundRuleInfoList(), (o1, o2) -> o1.getOrderType().compareTo(o2.getOrderType()));
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        fundAccountDetail.setFundRuleInfoList(fundRuleBody.getFundRuleInfoList());
        fundAccountDetail.setAccountDetail(accountDetail);

        return fundAccountDetail;
    }

    /**
     * Generic Method to mappingResponse
     *
     * @param fundRuleBody
     * @param fundHolidayBody
     * @param responseCommon
     * @param responseCustomerExp
     * @return FundPaymentDetailRs
     */
    public FundPaymentDetailResponse mappingPaymentResponse(FundRuleBody fundRuleBody,
                                                            FundHolidayBody fundHolidayBody,
                                                            List<CommonData> responseCommon,
                                                            String responseCustomerExp) {
        if (StringUtils.isEmpty(fundRuleBody)
                || StringUtils.isEmpty(responseCustomerExp)) {
            return null;
        } else {
            FundPaymentDetailResponse fundPaymentDetailResponse = new FundPaymentDetailResponse();
            if (!StringUtils.isEmpty(fundHolidayBody)) {
                FundHolidayClassList fundHolidayUnit;
                List<FundHolidayClassList> fundHolidayClassList = new ArrayList<>();
                List<FundHolidayClassList> fundHolidayClassListRs = fundHolidayBody.getFundClassList();
                for (FundHolidayClassList fundHoliday : fundHolidayClassListRs) {
                    fundHolidayUnit = new FundHolidayClassList();
                    fundHolidayUnit.setFundCode(fundHoliday.getFundCode());
                    fundHolidayUnit.setFundHouseCode(fundHoliday.getFundHouseCode());
                    fundHolidayUnit.setHolidayDate(fundHoliday.getHolidayDate());
                    fundHolidayUnit.setHolidayDesc(fundHoliday.getHolidayDesc());
                    fundHolidayClassList.add(fundHolidayUnit);
                }
                fundPaymentDetailResponse.setFundHolidayList(fundHolidayClassList);
            }

            FundRule fundRule = new FundRule();
            List<FundRuleInfoList> fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundPaymentDetailResponse.setFundRule(fundRule);
            fundPaymentDetailResponse.setDepositAccountList(mappingAccount(responseCommon, responseCustomerExp));
            return fundPaymentDetailResponse;
        }
    }

    /**
     * Generic Method to mappingAccount
     *
     * @param responseCommon
     * @param responseCustomerExp
     * @return FundPaymentDetailRs
     */
    public static List<DepositAccount> mappingAccount(List<CommonData> responseCommon, String responseCustomerExp) {
        List<DepositAccount> depositAccountList = new ArrayList<>();

        try {
            JsonNode node;
            ObjectMapper mapper = new ObjectMapper();
            node = mapper.readValue(responseCustomerExp, JsonNode.class);
            ArrayNode arrayNode = (ArrayNode) node.get("data");
            int size = arrayNode.size();
            DepositAccount depositAccount;
            List<String> eligibleAccountCodeBuy = responseCommon.get(0).getEligibleAccountCodeBuy();
            for (int i = 0; i < size; i++) {
                JsonNode itr = arrayNode.get(i);
                String accCode = itr.get("product_code").textValue();
                for (String productCode : eligibleAccountCodeBuy) {
                    if (productCode.equals(accCode)) {
                        depositAccount = new DepositAccount();
                        depositAccount.setAccountNumber(itr.get("account_number_display").textValue());
                        depositAccount.setAccountStatus(itr.get("account_status_text").textValue());
                        String accType = itr.get("product_group_code").textValue();
                        depositAccount.setAccountType(convertAccountType(accType));
                        depositAccount.setAccountTypeShort(accType);
                        depositAccount.setProductNameEN(itr.get("product_name_Eng").textValue());
                        depositAccount.setProductNameTH(itr.get("product_name_TH").textValue());
                        depositAccount.setAvailableBalance(new BigDecimal(itr.get("current_balance").textValue()));
                        String accStatusCode = itr.get("account_status_code").textValue();
                        depositAccount.setAccountStatusCode(accStatusCode);
                        depositAccountList.add(depositAccount);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
        }
        return depositAccountList;
    }

    /**
     * Generic Method to convert Account Type form 3 digits to 1 digit
     *
     * @param productType
     * @return String Account Type
     */
    public static String convertAccountType(String productType) {
        String accType = "";
        switch (productType) {
            case ProductsExpServiceConstant.ACC_TYPE_SDA:
                accType = ProductsExpServiceConstant.ACC_TYPE_SAVING;
                break;
            case ProductsExpServiceConstant.ACC_TYPE_DDA:
                accType = ProductsExpServiceConstant.ACC_TYPE_CURRENT;
                break;
            default:
                accType = "";
        }
        return accType;
    }

    /**
     * Generic Method to Get Current Date with Format
     *
     * @param startTime the start HHMM
     * @param endTime   the end HHMM
     * @return boolean
     */
    public static boolean isBusinessClose(String startTime, String endTime) {
        boolean isClose = false;
        try {
            if (!StringUtils.isEmpty(startTime)
                    && !StringUtils.isEmpty(endTime)) {
                Calendar calCurrent = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(ProductsExpServiceConstant.MF_TIME_WITH_COLON_HHMM);
                String getCurrentTime = sdf.format(calCurrent.getTime());
                if (isTimeBetweenTwoTime(startTime, endTime, getCurrentTime)) {
                    isClose = true;
                }
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
        }
        return isClose;
    }

    /**
     * Generic Method to create HTTP Header
     *
     * @param correlationId
     * @return
     */
    public static Map<String, Object> createHeader(String correlationId, int pageSize, int pageNo) {
        Map<String, Object> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
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
        Map<String, String> investmentHeader = new HashMap<>();
        investmentHeader.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        return investmentHeader;
    }

    /**
     * Method to check suitability is expire from MF service
     *
     * @param suitabilityInfo
     * @return
     */
    public static boolean isSuitabilityExpire(SuitabilityInfo suitabilityInfo) {
        boolean isExpire = true;
        try {
            if (!StringUtils.isEmpty(suitabilityInfo)
                    && suitabilityInfo.getSuitValidation().equals(ProductsExpServiceConstant.SUITABILITY_EXPIRED)) {
                return isExpire;
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
        }
        return false;
    }

    /**
     * Method to check citizen id expire with current date
     *
     * @param customerProfileResponseData
     * @return
     */
    public static boolean isCustIDExpired(CustGeneralProfileResponse customerProfileResponseData) {
        try {
            if (!StringUtils.isEmpty(customerProfileResponseData) && customerProfileResponseData.getIdExpireDate() != null) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(ProductsExpServiceConstant.MF_DATE_YYYYMMDD);
                String getCurrentTime = sdf.format(cal.getTime());
                return getCurrentTime.compareTo(customerProfileResponseData.getIdExpireDate()) > 0;
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
        }
        return false;
    }

    /**
     * Method to check account status is dormant and acc balance is not 0
     *
     * @param responseCustomerExp
     * @return boolean
     */
    public static boolean isCASADormant(String responseCustomerExp) {
        if (StringUtils.isEmpty(responseCustomerExp)) {
            return true;
        } else {
            try {
                JsonNode node;
                ObjectMapper mapper = new ObjectMapper();
                node = mapper.readValue(responseCustomerExp, JsonNode.class);
                ArrayNode arrayNode = (ArrayNode) node.get("data");
                int size = arrayNode.size();
                List<Integer> countDormant = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JsonNode itr = arrayNode.get(i);
                    String accStatus = itr.get("account_status_code").textValue();
                    BigDecimal balance = new BigDecimal(itr.get("current_balance").textValue());
                    BigDecimal zeroBalance = new BigDecimal("0");
                    switch (accStatus) {
                        case ProductsExpServiceConstant.ACTIVE_STATUS_CODE:
                        case ProductsExpServiceConstant.INACTIVE_STATUS_CODE:
                            if ((balance.compareTo(zeroBalance) == 0)) countDormant.add(i);
                            break;
                        case ProductsExpServiceConstant.DORMANT_STATUS_CODE:
                            countDormant.add(i);
                            break;
                        default:
                            break;
                    }
                }
                return (size == countDormant.size());
            } catch (JsonProcessingException e) {
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            }
            return false;
        }
    }

    /**
     * Generic Method to delete Colon from time
     *
     * @param timeHHmm
     * @return String
     */
    public static String deleteColonDateFormat(String timeHHmm) {
        String changeTime = "";
        if (!StringUtils.isEmpty(timeHHmm)) {
            return timeHHmm.replace(":", "");
        }
        return changeTime;
    }

    /**
     * Generic Method to mappingFundListData
     *
     * @param fundClass
     * @return List<FundClass>
     */
    public static List<FundClass> mappingFundListData(List<FundClass> fundClass) {
        List<FundClass> fundClassData = new ArrayList<>();
        try {
            for (FundClass fundClassLoop : fundClass) {
                List<FundHouse> fundHouseList = fundClassLoop.getFundHouseList();
                for (FundHouse fundHouse : fundHouseList) {
                    FundList fundList = fundHouse.getFundList();
                    List<Fund> fund = fundList.getFund();
                    fundHouse.setFund(fund);
                }
                fundClassData.add(fundClassLoop);
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
        }
        return fundClassData;
    }

    /**
     * Generic Method to mappingFundSearchListData
     *
     * @param fundClass
     * @return List<FundSearch>
     */
    public static List<FundSearch> mappingFundSearchListData(List<FundClass> fundClass) {
        FundSearch fundSearch;
        List<FundSearch> searchList = new ArrayList<>();
        List<FundSearch> fundListDistinctByFundCode = new ArrayList<>();
        try {
            for (FundClass fundClassLoop : fundClass) {
                List<FundHouse> fundHouseList = fundClassLoop.getFundHouseList();
                for (FundHouse fundHouse : fundHouseList) {
                    FundList fundList = fundHouse.getFundList();
                    List<Fund> fund = fundList.getFund();
                    fundHouse.setFund(fund);
                    for (Fund fundLoop : fundHouse.getFundList().getFund()) {
                        fundSearch = new FundSearch();
                        fundSearch.setFundHouseCode(fundHouse.getFundHouseCode());
                        fundSearch.setFundShortName(fundLoop.getFundShortName());
                        fundSearch.setFundNameEN(fundLoop.getFundNameEN());
                        fundSearch.setFundNameTH(fundLoop.getFundNameTH());
                        fundSearch.setFundNickNameEN(fundLoop.getFundNickNameEN());
                        fundSearch.setFundNickNameTH(fundLoop.getFundNickNameTH());
                        fundSearch.setFundCode(fundLoop.getFundCode());
                        fundSearch.setPortfolioNumber(fundLoop.getPortfolioNumber());
                        searchList.add(fundSearch);
                    }
                    fundHouse.setFundList(null);
                }
            }
            Set<String> nameSet = new HashSet<>();
            fundListDistinctByFundCode = searchList.stream().filter(e -> nameSet.add(e.getFundCode())).collect(Collectors.toList());
            return fundListDistinctByFundCode;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
        }
        return searchList;
    }

    /**
     * Generic Method to mappingRequestFundAcc
     *
     * @param fundAccountRequest
     * @return FundAccountRequestBody
     */
    public static FundAccountRequestBody mappingRequestFundAcc(FundAccountRequest fundAccountRequest) {
        FundAccountRequestBody fundAccountRequestBody = new FundAccountRequestBody();
        fundAccountRequestBody.setFundCode(fundAccountRequest.getFundCode());
        fundAccountRequestBody.setPortfolioNumber(fundAccountRequest.getPortfolioNumber());
        return fundAccountRequestBody;
    }

    /**
     * Generic Method to mappingRequestFundRule
     *
     * @param fundAccountRequest
     * @return FundRuleRequestBody
     */
    public static FundRuleRequestBody mappingRequestFundRule(Object fundAccountRequest) {
        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        if (fundAccountRequest instanceof FundAccountRequest) {
            FundAccountRequest fundAccount = (FundAccountRequest) fundAccountRequest;
            fundRuleRequestBody.setFundCode(fundAccount.getFundCode());
            fundRuleRequestBody.setFundHouseCode(fundAccount.getFundHouseCode());
            fundRuleRequestBody.setTranType(fundAccount.getTranType());
        } else if (fundAccountRequest instanceof FundPaymentDetailRequest) {
            FundPaymentDetailRequest fundAccount = (FundPaymentDetailRequest) fundAccountRequest;
            fundRuleRequestBody.setFundCode(fundAccount.getFundCode());
            fundRuleRequestBody.setFundHouseCode(fundAccount.getFundHouseCode());
            fundRuleRequestBody.setTranType(fundAccount.getTranType());
        }
        return fundRuleRequestBody;
    }

    /**
     * Generic Method to mappingRequestStmtByPort
     *
     * @param fundAccountRequest
     * @return OrderStmtByPortRq
     */
    public static OrderStmtByPortRequest mappingRequestStmtByPort(FundAccountRequest fundAccountRequest, String startPage, String endPage) {
        OrderStmtByPortRequest orderStmtByPortRequest = new OrderStmtByPortRequest();
        orderStmtByPortRequest.setPortfolioNumber(fundAccountRequest.getPortfolioNumber());
        orderStmtByPortRequest.setFundCode(fundAccountRequest.getFundCode());
        orderStmtByPortRequest.setRowStart(startPage);
        orderStmtByPortRequest.setRowEnd(endPage);
        return orderStmtByPortRequest;
    }

    /**
     * Generic Method to mappingRequestStmtByPort
     *
     * @param fundFactSheetRequestBody
     * @return AlternativeRq
     */
    public static AlternativeRequest mappingRequestAlternative(FundFactSheetRequestBody fundFactSheetRequestBody) {
        AlternativeRequest alternativeRequest = new AlternativeRequest();
        alternativeRequest.setCrmId(fundFactSheetRequestBody.getCrmId());
        alternativeRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        alternativeRequest.setOrderType(fundFactSheetRequestBody.getOrderType());
        alternativeRequest.setFundClassThaiHubName(fundFactSheetRequestBody.getFundClassThaiHubName());
        return alternativeRequest;
    }

    @SuppressWarnings("all")
    public static TmbOneServiceResponse mapTmbOneServiceResponse(Optional<ByteBuffer> optionalResponse) {
        try {
            if (!optionalResponse.isPresent()) {
                return null;
            }
            String respBody = StandardCharsets.UTF_8.decode(optionalResponse.get()).toString();
            return (TmbOneServiceResponse) TMBUtils.convertStringToJavaObj(respBody, TmbOneServiceResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error received, cannot parse.");
            return null;
        }
    }

    /**
     * Generic Method to mappingCache
     *
     * @param jsonStr
     * @param key
     * @return CacheModel
     */
    public static CacheModel mappingCache(String jsonStr, String key) {
        CacheModel cacheModel = new CacheModel();
        cacheModel.setKey(key);
        cacheModel.setTtl(ProductsExpServiceConstant.INVESTMENT_CACHE_TIME_EXPIRE);
        cacheModel.setValue(jsonStr);
        return cacheModel;
    }

    /**
     * Generic Method to mappingFollowingFlag
     *
     * @param fundClassList
     * @param customerFavoriteFundDataList
     * @return List<FundClassList>
     */
    public static List<FundClassListInfo> mappingFollowingFlag(List<FundClassListInfo> fundClassList, List<CustomerFavoriteFundData> customerFavoriteFundDataList) {
        List<FundClassListInfo> fundClassLists = new ArrayList<>();
        for (FundClassListInfo fundClass : fundClassList) {
            for (CustomerFavoriteFundData customerFavoriteFund : customerFavoriteFundDataList) {
                if (customerFavoriteFund.getFundCode().equals(fundClass.getFundCode())) {
                    fundClass.setFollowingFlag(ProductsExpServiceConstant.PROCESS_FLAG_Y);
                }
            }
            if (StringUtils.isEmpty(fundClass.getFollowingFlag())) {
                fundClass.setFollowingFlag(ProductsExpServiceConstant.BUSINESS_HR_CLOSE);
            }
            fundClassLists.add(fundClass);
        }
        return fundClassLists;
    }

    /**
     * Generic Method to mappingBoughtFlag
     *
     * @param fundClassList
     * @param fundSummaryResponse
     * @return List<FundClassList>
     */
    public static List<FundClassListInfo> mappingBoughtFlag(List<FundClassListInfo> fundClassList, FundSummaryResponse fundSummaryResponse) {
        List<FundClassListInfo> fundClassLists = new ArrayList<>();
        try {
            for (FundClassListInfo fundClass : fundClassList) {
                for (FundClass fundClassLoop : fundSummaryResponse.getBody().getFundClassList().getFundClass()) {
                    List<FundHouse> fundHouseList = fundClassLoop.getFundHouseList();
                    mappingBoughtFlagWithFundHouse(fundClass, fundHouseList);
                }
                if (StringUtils.isEmpty(fundClass.getBoughtFlag())) {
                    fundClass.setBoughtFlag(ProductsExpServiceConstant.BUSINESS_HR_CLOSE);
                }
                fundClassLists.add(fundClass);
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
        }
        return fundClassLists;
    }

    /**
     * Generic Method to mappingBoughtFlagWithFundHouse
     *
     * @param fundClass
     * @param fundHouseList
     * @return FundClassListInfo
     */
    public static FundClassListInfo mappingBoughtFlagWithFundHouse(FundClassListInfo fundClass, List<FundHouse> fundHouseList) {
        try {
            for (FundHouse fundHouse : fundHouseList) {
                FundList fundList = fundHouse.getFundList();
                List<Fund> fund = fundList.getFund();
                for (Fund fundDetail : fund) {
                    if (fundDetail.getFundCode().equals(fundClass.getFundCode())) {
                        fundClass.setBoughtFlag(ProductsExpServiceConstant.PROCESS_FLAG_Y);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
        }
        return fundClass;
    }

    /**
     * @param crmId
     * @return
     */
    public static String fullCrmIdFormat(String crmId) {
        if (crmId.length() > 14) {
            return crmId;
        }
        DecimalFormat decimalFormat = new DecimalFormat(ProductsExpServiceConstant.CRM_ID_FORMAT);
        return ProductsExpServiceConstant.CRM_ID_PREFIX.concat(decimalFormat.format(Double.parseDouble(crmId)));
    }

    /**
     * @param crmId
     * @return half digit of crmId
     */
    public static String halfCrmIdFormat(String crmId) {
        if (crmId.length() <= 14) {
            return crmId;
        }
        return crmId.substring(crmId.length() - ProductsExpServiceConstant.DIGIT_OF_CRM_ID);
    }
}
