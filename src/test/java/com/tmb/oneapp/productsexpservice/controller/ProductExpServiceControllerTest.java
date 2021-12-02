package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.*;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProductExpServiceControllerTest {

    @Mock
    private ProductsExpService productsExpService;

    @InjectMocks
    private ProductExpServiceController productExpServiceController;

    private final String success_code = "0000";

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000000028365";

    private AccountDetailResponse accountDetailResponse = null;

    private FundRuleResponse fundRuleResponse = null;

    private FundAccountResponse fundAccountResponse = null;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private FundAccountDetail mappingResponse(AccountDetailResponse accountDetailResponse, FundRuleResponse fundRuleResponse) {
        FundRule fundRule = new FundRule();
        List<FundRuleInfoList> fundRuleInfoList;
        AccountDetail accountDetail = new AccountDetail();
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        if (!StringUtils.isEmpty(fundRuleResponse)) {
            fundRuleInfoList = fundRuleResponse.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundRule.setIpoflag(ruleInfoList.getIpoflag());
            BeanUtils.copyProperties(accountDetail, accountDetailResponse.getFundDetail());
            fundAccountDetail.setFundRuleInfoList(fundRuleInfoList);
            fundAccountDetail.setAccountDetail(accountDetail);
        }
        return fundAccountDetail;
    }

    private void initAccountDetailResponse() {
        accountDetailResponse = new AccountDetailResponse();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("TTTTT");
        fundDetail.setFundHouseCode("EEEEE");
        accountDetailResponse.setFundDetail(fundDetail);
    }

    private void initFundRuleResponse() {
        fundRuleResponse = new FundRuleResponse();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleResponse.setFundRuleInfoList(fundRuleInfoList);
    }

    private void initSuccessResponseAccountDetail() {
        fundAccountResponse = new FundAccountResponse();
        FundAccountDetail details = new FundAccountDetail();
        FundRule fundRule = new FundRule();
        fundRule.setIpoflag("N");
        fundRule.setFundAllowOtx("Y");
        fundRule.setFundCode("TESEQDSSFX");
        fundRule.setRiskRate("1");
        fundRule.setFundHouseCode("TFUND");
        fundRule.setOrderType("1");
        fundRule.setAllotType("3");
        fundRule.setAllowAipFlag("Y");
        fundRule.setDateAfterIpo("20200413");
        fundRule.setFrontEndFee("0");
        details.setFundRuleInfoList(null);

        AccountDetail accountDetail = new AccountDetail();
        List<FundOrderHistory> ordersHistories = new ArrayList<>();
        FundOrderHistory fundOrderHistory = new FundOrderHistory();
        fundOrderHistory.setAmount("2000.00");
        fundOrderHistory.setOrderDate("20200413");

        FundOrderHistory fundOrderHistoryOne = new FundOrderHistory();
        fundOrderHistoryOne.setAmount("2000.00");
        fundOrderHistoryOne.setOrderDate("20200413");

        ordersHistories.add(fundOrderHistory);
        ordersHistories.add(fundOrderHistoryOne);
        accountDetail.setOrdersHistories(ordersHistories);

        details.setAccountDetail(accountDetail);
        fundAccountResponse.setDetails(details);
    }

    @Test
    public void testGetFundAccountDetailFullReturn() throws TMBCommonException {
        initSuccessResponseAccountDetail();
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(correlationId,"crmid", fundAccountRequest)).thenReturn(fundAccountResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId,"crmid", fundAccountRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(success_code, actualResult.getBody().getStatus().getCode());

        FundAccountResponse newResponse = actualResult.getBody().getData();
        assertEquals(fundAccountResponse, newResponse);
        assertEquals(fundAccountResponse.getDetails().getAccountDetail().getOrdersHistories().size(),
                newResponse.getDetails().getAccountDetail().getOrdersHistories().size());
    }

    @Test
    public void testGetFundAccountDetailNotFound() throws TMBCommonException {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(correlationId,"crmid", fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId,"crmid", fundAccountRequest);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void testGetFundAccountDetail() throws TMBCommonException {
        initAccountDetailResponse();
        initFundRuleResponse();

        FundAccountResponse fundAccountResponse = new FundAccountResponse();
        ;
        FundAccountRequest fundAccountRequest = new FundAccountRequest();

        AccountDetailResponse accountDetailResponse = null;
        FundRuleResponse fundRuleResponse = null;

        try {
            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailResponse, fundRuleResponse);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(correlationId,"crmid", fundAccountRequest)).thenReturn(fundAccountResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId,"crmid", fundAccountRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getDetails());
    }

    @Test
    public void testGetFundPrePaymentDetailNotNull() throws TMBCommonException {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        FundPaymentDetailResponse fundPaymentDetailResponse;

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundPaymentDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_payment_detail.json").toFile(), FundPaymentDetailResponse.class);
            TmbOneServiceResponse<FundPaymentDetailResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
            tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
            tmbOneServiceResponse.setData(fundPaymentDetailResponse);
            when(productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest)).thenReturn(tmbOneServiceResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> actualResult = productExpServiceController
                .getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getFundRule());
        Assert.assertNotNull(actualResult.getBody().getData().getDepositAccountList());
        Assert.assertNotNull(actualResult.getBody().getData().getFundHolidayList());
    }

    @Test
    public void testGetFundAccountDetailNull() throws TMBCommonException {
        initAccountDetailResponse();
        initFundRuleResponse();

        FundAccountResponse fundAccountResponse = new FundAccountResponse();
        FundAccountRequest fundAccountRequest = new FundAccountRequest();

        AccountDetailResponse accountDetailResponse = null;
        FundRuleResponse fundRuleResponse = null;

        try {
            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailResponse, fundRuleResponse);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(correlationId,"crmid", fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId,"crmid", fundAccountRequest);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundListException() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        when(productsExpService.getFundList(correlationId, crmId, fundListRequest)).thenReturn(null);

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(correlationId, crmId, fundListRequest);

        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundListNotFound() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        try {
            when(productsExpService.getFundList(correlationId, crmId, fundListRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(correlationId, crmId, fundListRequest);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundList() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        List<FundClassListInfo> list = new ArrayList<>();
        FundClassListInfo fundClassListInfo = new FundClassListInfo();
        fundClassListInfo.setFundCode("ABCC");
        list.add(fundClassListInfo);

        try {
            when(productsExpService.getFundList(correlationId, crmId, fundListRequest)).thenReturn(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(correlationId, crmId, fundListRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    void testDataNotFoundError() {
        TmbOneServiceResponse<FundPaymentDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setService("products-exp-service");
        oneServiceResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> response = productExpServiceController.dataNotFoundError(oneServiceResponse);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetFundPrePaymentDetailNull() throws TMBCommonException {
        TmbOneServiceResponse<FundPaymentDetailResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(productsExpService.getFundPrePaymentDetail(anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> result = productExpServiceController.getFundPrePaymentDetail(
                "correlationId", "crmId", new FundPaymentDetailRequest());
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    void should_return_casa_dormant_error_code_when_call_get_fund_pre_payment_detail_given_fundpayment_request() throws TMBCommonException {
        TmbOneServiceResponse<FundPaymentDetailResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        tmbOneServiceResponse.setStatus(status);
        tmbOneServiceResponse.setData(null);
        when(productsExpService.getFundPrePaymentDetail(anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> result = productExpServiceController.getFundPrePaymentDetail(
                "correlationId", "crmId", new FundPaymentDetailRequest());
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
        Assert.assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(),
                result.getBody().getStatus().getCode());
    }

    @Test
    void should_return_SuggestAllocationDTO_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() throws IOException, TMBCommonException {
        //Given
        ObjectMapper mapper = new ObjectMapper();

        SuggestAllocationDTO suggestAllocationDTO = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation_dto.json").toFile(), SuggestAllocationDTO.class);
        when(productsExpService.getSuggestAllocation(correlationId, crmId)).thenReturn(suggestAllocationDTO);

        //When
        ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> actual = productExpServiceController.getFundSuggestAllocation(correlationId, crmId);

        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(suggestAllocationDTO, actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() throws TMBCommonException {
        //Given
        when(productsExpService.getSuggestAllocation(correlationId, crmId)).thenReturn(null);

        //When
        ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> actual = productExpServiceController.getFundSuggestAllocation(correlationId, crmId);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }
}
