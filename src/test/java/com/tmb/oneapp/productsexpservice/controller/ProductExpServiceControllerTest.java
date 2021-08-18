package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.*;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetData;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetValidationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
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

    private AccountDetailBody accountDetailBody = null;

    private FundRuleBody fundRuleBody = null;

    private FundAccountResponse fundAccountResponse = null;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private FundAccountDetail mappingResponse(AccountDetailBody accountDetailBody, FundRuleBody fundRuleBody) {
        FundRule fundRule = new FundRule();
        List<FundRuleInfoList> fundRuleInfoList = null;
        AccountDetail accountDetail = new AccountDetail();
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        if (!StringUtils.isEmpty(fundRuleBody)) {
            fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundRule.setIpoflag(ruleInfoList.getIpoflag());
            BeanUtils.copyProperties(accountDetail, accountDetailBody.getFundDetail());
            fundAccountDetail.setFundRuleInfoList(fundRuleInfoList);
            fundAccountDetail.setAccountDetail(accountDetail);
        }
        return fundAccountDetail;
    }

    private void initAccountDetailBody() {
        accountDetailBody = new AccountDetailBody();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("TTTTT");
        fundDetail.setFundHouseCode("EEEEE");
        accountDetailBody.setFundDetail(fundDetail);
    }

    private void initFundRuleBody() {
        fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleBody.setFundRuleInfoList(fundRuleInfoList);
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
    public void testGetFundAccountDetailFullReturn() {
        initSuccessResponseAccountDetail();
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(correlationId, fundAccountRequest)).thenReturn(fundAccountResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId, fundAccountRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(success_code, actualResult.getBody().getStatus().getCode());

        FundAccountResponse newResponse = actualResult.getBody().getData();
        assertEquals(fundAccountResponse, newResponse);
        assertEquals(fundAccountResponse.getDetails().getAccountDetail().getOrdersHistories().size(),
                newResponse.getDetails().getAccountDetail().getOrdersHistories().size());
    }

    @Test
    public void testGetFundAccountDetailNotFound() {
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        try {
            when(productsExpService.getFundAccountDetail(correlationId, fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId, fundAccountRequest);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void testGetFundAccountDetail() {
        initAccountDetailBody();
        initFundRuleBody();

        FundAccountResponse fundAccountResponse = null;
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        AccountDetailBody accountDetailBody = null;
        FundRuleBody fundRuleBody = null;

        try {
            fundAccountResponse = new FundAccountResponse();

            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailBody, fundRuleBody);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(correlationId, fundAccountRequest)).thenReturn(fundAccountResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId, fundAccountRequest);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getDetails());
    }

    @Test
    public void testGetFundPrePaymentDetailNotNull() {
        FundPaymentDetailRequest fundPaymentDetailRequest = new FundPaymentDetailRequest();
        fundPaymentDetailRequest.setFundCode("SCBTMF");
        fundPaymentDetailRequest.setFundHouseCode("SCBAM");
        fundPaymentDetailRequest.setTranType("1");

        FundPaymentDetailResponse fundPaymentDetailResponse;

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundPaymentDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_payment_detail.json").toFile(), FundPaymentDetailResponse.class);
            when(productsExpService.getFundPrePaymentDetail(correlationId, crmId, fundPaymentDetailRequest)).thenReturn(fundPaymentDetailResponse);

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
    public void testGetFundAccountDetailNull() {
        initAccountDetailBody();
        initFundRuleBody();

        FundAccountResponse fundAccountResponse;
        FundAccountRequest fundAccountRequest = new FundAccountRequest();
        fundAccountRequest.setFundCode("EEEEEEE");
        fundAccountRequest.setFundHouseCode("TTTTTTT");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setPortfolioNumber("PT00000001111");
        fundAccountRequest.setTranType("All");

        AccountDetailBody accountDetailBody = null;
        FundRuleBody fundRuleBody = null;

        try {
            fundAccountResponse = new FundAccountResponse();

            FundAccountDetail fundAccountDetail = mappingResponse(accountDetailBody, fundRuleBody);
            fundAccountResponse.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(correlationId, fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountResponse>> actualResult = productExpServiceController
                .getFundAccountDetail(correlationId, fundAccountRequest);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundFactSheetAndValidation() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("SCBTMF");
        fundFactSheetRequestBody.setFundHouseCode("SCBAM");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("Y");
        fundFactSheetRequestBody.setOrderType("1");

        FundFactSheetValidationResponse fundRsAndValidation;

        try {
            fundRsAndValidation = new FundFactSheetValidationResponse();
            FundFactSheetData body = new FundFactSheetData();
            body.setFactSheetData("fdg;klghbdf;jbneoa;khnd'flbkndflkhnreoid;bndzfklbnoresibndlzfk[bnseriohnbodkzfvndsogb");
            fundRsAndValidation.setBody(body);
            when(productsExpService.validateAlternativeBuyFlow(any(), any(), any())).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> actualResult = productExpServiceController
                .getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void getFundFactSheetAndValidationFail() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("SCBTMF");
        fundFactSheetRequestBody.setFundHouseCode("SCBAM");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("Y");
        fundFactSheetRequestBody.setOrderType("1");

        FundFactSheetValidationResponse fundRsAndValidation;
        try {
            fundRsAndValidation = new FundFactSheetValidationResponse();
            fundRsAndValidation.setError(true);
            fundRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            fundRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_MESSAGE);
            fundRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_DESC);

            when(productsExpService.validateAlternativeBuyFlow(any(),any(),any())).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> actualResult = productExpServiceController
                .getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFactSheetAndValidationError() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("SCBTMF");
        fundFactSheetRequestBody.setFundHouseCode("SCBAM");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setOrderType("1");

        ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> actualResult = productExpServiceController
                .getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFactSheetAndValidationException() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("SCBTMF");
        fundFactSheetRequestBody.setFundHouseCode("SCBAM");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("Y");
        fundFactSheetRequestBody.setOrderType("1");

        when(productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> actualResult = productExpServiceController
                .getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitchException() {
        when(productsExpService.validateAlternativeSellAndSwitch(correlationId, crmId)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(correlationId, crmId);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitchError() {
        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode("SCBTMF");
        alternativeBuyRequest.setFundHouseCode("SCBAM");
        alternativeBuyRequest.setProcessFlag("Y");
        alternativeBuyRequest.setOrderType("2");
        alternativeBuyRequest.setUnitHolderNumber("PT00000000000");

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(correlationId, crmId);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitch() {
        FundResponse fundRsAndValidation;

        try {
            fundRsAndValidation = new FundResponse();
            fundRsAndValidation.setError(false);
            fundRsAndValidation.setErrorCode("0000");
            fundRsAndValidation.setErrorDesc("success");
            fundRsAndValidation.setErrorMsg("success");

            when(productsExpService.validateAlternativeSellAndSwitch(correlationId, crmId)).thenReturn(fundRsAndValidation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(correlationId, crmId);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void getFundListException() {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRequest fundListRequest = new FundListRequest();
        fundListRequest.setUnitHolderNumber(unitStr);

        when(productsExpService.getFundList(correlationId, crmId, fundListRequest)).thenThrow(MockitoException.class);

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
    void testErrorResponse() {
        TmbOneServiceResponse<FundResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        FundResponse data = new FundResponse();
        data.setError(true);
        oneServiceResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<FundResponse>> errorResponse = productExpServiceController.errorResponse(oneServiceResponse, data);
        assertEquals(400, errorResponse.getStatusCodeValue());
    }

    @Test
    void testGetFundPrePaymentDetailNull() {
        when(productsExpService.getFundPrePaymentDetail(anyString(), anyString(), any())).thenReturn(null);

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailResponse>> result = productExpServiceController.getFundPrePaymentDetail(
                "correlationId", "crmId", new FundPaymentDetailRequest());
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    void should_return_SuggestAllocationDTO_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() throws IOException {
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
    void should_return_not_found_when_call_get_fund_suggest_allocation_given_correlation_id_and_crd_id() {
        //Given
        when(productsExpService.getSuggestAllocation(correlationId, crmId)).thenThrow(RuntimeException.class);

        //When
        ResponseEntity<TmbOneServiceResponse<SuggestAllocationDTO>> actual = productExpServiceController.getFundSuggestAllocation(correlationId, crmId);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }
}
