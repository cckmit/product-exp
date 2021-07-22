package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClass;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSearch;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundOrderHistory;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementList;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(JUnit4.class)
public class UtilMapTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidateTMBResponse() {
        /* body  */
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setFundEnglishName("card");
        fundDetail.setInvestmentValue("12345");

        AccountDetailBody body = new AccountDetailBody();
        body.setFundDetail(fundDetail);

        /* fundRuleBody */
        FundRuleBody fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleBody.setFundRuleInfoList(fundRuleList);

        /* statementResponse */
        List<StatementList> list = new ArrayList<>();
        for (StatementList statementList : list) {
            statementList.setTranTypeEN("Normal");
            statementList.setFundCode("1234");
            statementList.setEffectiveDate("28-03-2021");
            list.add(statementList);
        }
        StatementResponse statementResponse = new StatementResponse();
        statementResponse.setTotalRecord("10");
        statementResponse.setStatementList(list);

        FundAccountResponse result = UtilMap.validateTMBResponse(body, fundRuleBody, statementResponse);
        List<FundOrderHistory> ordersHistories = result.getDetails().getAccountDetail().getOrdersHistories();
        Assert.assertEquals(true, ordersHistories.isEmpty());
    }

    @Test
    public void testMappingResponse() {
        /* body */
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setFundEnglishName("card");
        fundDetail.setInvestmentValue("12345");

        AccountDetailBody body = new AccountDetailBody();
        body.setFundDetail(fundDetail);

        /* fundRuleBody */
        FundRuleBody fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleBody.setFundRuleInfoList(fundRuleList);

        /* statementResponse */
        StatementResponse statementResponse = new StatementResponse();
        statementResponse.setTotalRecord("10");
        List<StatementList> list = new ArrayList<>();
        for (StatementList statementList : list) {
            statementList.setTranTypeEN("Normal");
            statementList.setFundCode("1234");
            statementList.setEffectiveDate("28-03-2021");
            list.add(statementList);
        }
        statementResponse.setStatementList(list);

        FundAccountDetail result = UtilMap.mappingResponse(body, fundRuleBody, statementResponse);
        List<FundRuleInfoList> fundRuleInfoList = result.getFundRuleInfoList();
        Assert.assertEquals(true, fundRuleInfoList.isEmpty());
    }

    @Test
    public void testMappingPaymentResponse() {
        FundRuleBody fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleBody.setFundRuleInfoList(fundRuleList);
        FundHolidayBody fundHolidayBody = new FundHolidayBody();
        List<FundHolidayClassList> list = new ArrayList<>();
        for (FundHolidayClassList fundHolidayClassList : list) {
            fundHolidayClassList.setFundCode("1234");
            fundHolidayClassList.setHolidayDesc("Enjoy");
            fundHolidayClassList.setHolidayDate("12-12-2012");
            fundHolidayClassList.setFundHouseCode("1234");
        }
        fundHolidayBody.setFundClassList(list);
        List<CommonData> responseCommon = new ArrayList();
        CommonData data = new CommonData();
        for (CommonData common : responseCommon) {
            common.setAccount221Url("www.gmail.com");
            common.setAccount290Url("www.123.com");
            responseCommon.add(common);
        }
        data.setAccount221Url("www.gmail.com");
        data.setChannel("1234");
        responseCommon.add(data);
        Assert.assertNotEquals("www.gmail.com", data.getAccount290Url());
    }

    @Test
    public void testMappingAccount() {
        CommonData data = new CommonData();
        data.setChannel("1234");
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundHouseCode("1234");
        FundPaymentDetailResponse fundPaymentDetailResponse = new FundPaymentDetailResponse();
        fundPaymentDetailResponse.setFundRule(list);
        List<DepositAccount> depositAccountList = UtilMap.mappingAccount(Arrays.asList(data), "responseCustomerExp");
        fundPaymentDetailResponse.setDepositAccountList(depositAccountList);
        data.setAccount290Url("1234");
        assertNotEquals(data.getAccount290Url(), depositAccountList);
    }

    @Test
    public void testConvertAccountType() {
        String result = UtilMap.convertAccountType("productType");
        Assert.assertEquals("", result);
    }

    @Test
    public void testIsBusinessClose() {
        boolean result = UtilMap.isBusinessClose("startTime", "endTime");
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCreateHeader() {
        Map<String, Object> result = UtilMap.createHeader("1234", 0, 0);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("X-Correlation-ID", "1234");
        hashMap.put("content-type", "application/json");
        hashMap.put("pageNo", "0");
        hashMap.put("pageSize", "0");
        Assert.assertNotEquals(hashMap, result);
    }

    @Test
    public void testCreateHeader2() {
        String correlationId = "1234";
        Map<String, String> result = UtilMap.createHeader(correlationId);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "1234");
        Assert.assertEquals(hashMap, result);
    }

    @Test
    public void testIsSuitabilityExpire() {
        SuitabilityInfo suitabilityInfo = new SuitabilityInfo();
        suitabilityInfo.setFxFlag("1234");
        suitabilityInfo.setSuitValidation("1");
        boolean result = UtilMap.isSuitabilityExpire(suitabilityInfo);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testIsCustIDExpired() {
        boolean result = UtilMap.isCustIDExpired(null);
        Assert.assertEquals(false, result);
    }

    @Test
    public void testIsCASADormant() {
        boolean result = UtilMap.isCASADormant("responseCustomerExp");
        Assert.assertEquals(false, result);
    }

    @Test
    public void testDeleteColonDateFormat() {
        String result = UtilMap.deleteColonDateFormat("timeHHmm");
        Assert.assertEquals("timeHHmm", result);
    }

    @Test
    public void testMappingFundListData() {
        FundClass fundClass = new FundClass();
        fundClass.setFundClassCode("1234");
        List<FundClass> result = UtilMap.mappingFundListData(Arrays.asList(fundClass));
        Assert.assertNotEquals(Arrays.asList(fundClass), result);
    }

    @Test
    public void testMappingFundSearchListData() {
        FundClass fundClass = new FundClass();
        fundClass.setFundClassCode("1234");
        List<FundSearch> result = UtilMap.mappingFundSearchListData(Arrays.asList(fundClass));
        FundSearch fundSearch = new FundSearch();
        fundSearch.setFundCode("1234");
        Assert.assertNotEquals(Arrays.asList(fundSearch), result);
    }

    @Test
    public void testMappingRequestFundAcc() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        FundAccountRequestBody result = UtilMap.mappingRequestFundAcc(fundAccountRequest);
        FundAccountRequestBody requestBody = new FundAccountRequestBody();
        requestBody.setFundCode("1234");
        Assert.assertEquals(requestBody.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMappingRequestFundRule() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        FundRuleRequestBody result = UtilMap.mappingRequestFundRule(fundAccountRequest);
        FundRuleRequestBody requestBody = new FundRuleRequestBody();
        requestBody.setFundCode("1234");
        Assert.assertEquals(requestBody.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMappingRequestStmtByPort() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("1234");
        OrderStmtByPortRequest result = UtilMap.mappingRequestStmtByPort(fundAccountRequest, "startPage", "endPage");
        OrderStmtByPortRequest portRequest = new OrderStmtByPortRequest();
        portRequest.setFundCode("1234");
        Assert.assertEquals(portRequest.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMappingRequestAlternative() {
        FfsRequestBody body = new FfsRequestBody();
        body.setFundCode("1234");
        AlternativeRequest result = UtilMap.mappingRequestAlternative(body);
        AlternativeRequest alternativeRequest = new AlternativeRequest();
        alternativeRequest.setFundCode("1234");
        Assert.assertEquals(alternativeRequest.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMapTmbOneServiceResponse() {
        TmbOneServiceResponse result = UtilMap.mapTmbOneServiceResponse(null);
        Assert.assertEquals(null, result);
    }

    @Test
    void should_return_full_format_of_crm_id_when_call_full_crm_id_format_given_half_crm_id() {
        // Given
        // When
        String actual = UtilMap.fullCrmIdFormat("00000000002914");

        // Then
        assertEquals("001100000000000000000000002914", actual);
    }

    @Test
    void should_return_full_format_of_crm_id_when_call_full_crm_id_format_given_full_crm_id() {
        // Given
        // When
        String actual = UtilMap.fullCrmIdFormat("001100000000000000000000002914");

        // Then
        assertEquals("001100000000000000000000002914", actual);
    }
}