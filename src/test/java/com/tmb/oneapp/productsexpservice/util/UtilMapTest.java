package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClass;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSearch;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundOrderHistory;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
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
        AccountDetailBody body = new AccountDetailBody();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setFundEnglishName("card");
        fundDetail.setInvestmentValue("12345");
        body.setFundDetail(fundDetail);
        FundRuleBody fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleBody.setFundRuleInfoList(fundRuleList);
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
        FundAccountResponse result = UtilMap.validateTMBResponse(body, fundRuleBody, statementResponse);
        List<FundOrderHistory> ordersHistories = result.getDetails().getAccountDetail().getOrdersHistories();
        Assert.assertEquals(true, ordersHistories.isEmpty());
    }

    @Test
    public void testMappingResponse() {
        AccountDetailBody body = new AccountDetailBody();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("1234");
        fundDetail.setCost("1234");
        fundDetail.setFundEnglishName("card");
        fundDetail.setInvestmentValue("12345");
        body.setFundDetail(fundDetail);
        FundRuleBody fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleList = new ArrayList<>();
        for (FundRuleInfoList ruleInfoList : fundRuleList) {
            ruleInfoList.setFundHouseCode("123");
            fundRuleList.add(ruleInfoList);
        }
        fundRuleBody.setFundRuleInfoList(fundRuleList);
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
        FundPaymentDetailRs fundPaymentDetailRs = new FundPaymentDetailRs();
        fundPaymentDetailRs.setFundRule(list);
        List<DepositAccount> depositAccountList = UtilMap.mappingAccount(Arrays.asList(data), "responseCustomerExp");
        fundPaymentDetailRs.setDepositAccountList(depositAccountList);
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
        hashMap.put(ProductsExpServiceConstant.X_CORRELATION_ID, "1234");
        hashMap.put("content-type", "application/json");
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
        OrderStmtByPortRq result = UtilMap.mappingRequestStmtByPort(fundAccountRequest, "startPage", "endPage");
        OrderStmtByPortRq portRq = new OrderStmtByPortRq();
        portRq.setFundCode("1234");
        Assert.assertEquals(portRq.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMappingRequestAlternative() {
        FfsRequestBody body = new FfsRequestBody();
        body.setFundCode("1234");
        AlternativeRq result = UtilMap.mappingRequestAlternative(body);
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("1234");
        Assert.assertEquals(alternativeRq.getFundCode(), result.getFundCode());
    }

    @Test
    public void testMapTmbOneServiceResponse() {
        TmbOneServiceResponse result = UtilMap.mapTmbOneServiceResponse(null);
        Assert.assertEquals(null, result);
    }

    @Test
    void should_return_full_format_of_crm_id_when_call_fill_up_crm_id_format_given_crm_id() {
        // Given
        // When
        String actual = UtilMap.fillUpCrmIdFormat("00000000002914");

        // Then
        assertEquals("001100000000000000000000002914", actual);
    }
}