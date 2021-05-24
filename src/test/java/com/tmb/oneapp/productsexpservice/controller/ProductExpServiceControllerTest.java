package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRq;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProductExpServiceControllerTest {
    @Mock
    ProductsExpService productsExpService;
    @InjectMocks
    ProductExpServiceController productExpServiceController;

    private final String success_code = "0000";
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private FundAccountRs fundAccountRs = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private FundAccountDetail mappingResponse(AccDetailBody accDetailBody, FundRuleBody fundRuleBody) {
        FundRule fundRule = new FundRule();
        List<FundRuleInfoList> fundRuleInfoList = null;
        AccountDetail accountDetail = new AccountDetail();
        FundAccountDetail fundAccountDetail = new FundAccountDetail();
        if (!StringUtils.isEmpty(fundRuleBody)) {
            fundRuleInfoList = fundRuleBody.getFundRuleInfoList();
            FundRuleInfoList ruleInfoList = fundRuleInfoList.get(0);
            BeanUtils.copyProperties(ruleInfoList, fundRule);
            fundRule.setIpoflag(ruleInfoList.getIpoflag());
            BeanUtils.copyProperties(accountDetail, accDetailBody.getDetailFund());
            fundAccountDetail.setFundRuleInfoList(fundRuleInfoList);
            fundAccountDetail.setAccountDetail(accountDetail);
        }

        return fundAccountDetail;
    }

    private void initAccDetailBody() {
        accDetailBody = new AccDetailBody();
        DetailFund detailFund = new DetailFund();
        detailFund.setFundHouseCode("TTTTT");
        detailFund.setFundHouseCode("EEEEE");
        accDetailBody.setDetailFund(detailFund);

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

    private void initSuccessResponseAccDetail() {
        fundAccountRs = new FundAccountRs();
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
        fundAccountRs.setDetails(details);

    }

    @Test
    public void testgetFundAccountDetailFullReturn() throws Exception {
        initSuccessResponseAccDetail();
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");
        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountRs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(success_code, actualResult.getBody().getStatus().getCode());

        FundAccountRs newResponse = actualResult.getBody().getData();
        assertEquals(fundAccountRs, newResponse);
        assertEquals(fundAccountRs.getDetails().getAccountDetail().getOrdersHistories().size(),
                newResponse.getDetails().getAccountDetail().getOrdersHistories().size());

    }

    @Test
    public void testgetFundAccountDetailNotfound() throws Exception {
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");
        try {
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void testgetFundAccountDetail() throws Exception {
        initAccDetailBody();
        initFundRuleBody();

        FundAccountRs fundAccountRs = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        AccDetailBody accDetailBody = null;
        FundRuleBody fundRuleBody = null;
        try {
            fundAccountRs = new FundAccountRs();

            FundAccountDetail fundAccountDetail = mappingResponse(accDetailBody, fundRuleBody);
            fundAccountRs.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(fundAccountRs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getDetails());
    }


    @Test
    public void testgetFundPrePaymentDetail() throws Exception {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        FundPaymentDetailRs fundPaymentDetailRs = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            fundPaymentDetailRs = mapper.readValue(Paths.get("src/test/resources/investment/fund_payment_detail.json").toFile(), FundPaymentDetailRs.class);
            when(productsExpService.getFundPrePaymentDetail(corrID, fundPaymentDetailRq)).thenReturn(fundPaymentDetailRs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> actualResult = productExpServiceController
                .getFundPrePaymentDetail(corrID, fundPaymentDetailRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        Assert.assertNotNull(actualResult.getBody().getData().getFundRule());
        Assert.assertNotNull(actualResult.getBody().getData().getDepositAccountList());
        Assert.assertNotNull(actualResult.getBody().getData().getFundHolidayList());
    }

    @Test
    public void testgetFundAccountDetailNull() throws Exception {
        initAccDetailBody();
        initFundRuleBody();

        FundAccountRs fundAccountRs = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundCode("EEEEEEE");
        fundAccountRq.setFundHouseCode("TTTTTTT");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT00000001111");
        fundAccountRq.setTranType("All");

        AccDetailBody accDetailBody = null;
        FundRuleBody fundRuleBody = null;
        try {
            fundAccountRs = new FundAccountRs();

            FundAccountDetail fundAccountDetail = mappingResponse(accDetailBody, fundRuleBody);
            fundAccountRs.setDetails(fundAccountDetail);
            when(productsExpService.getFundAccountDetail(corrID, fundAccountRq)).thenReturn(null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundAccountRs>> actualResult = productExpServiceController
                .getFundAccountDetail(corrID, fundAccountRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }


    @Test
    public void getFundFFSAndValidation() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        FfsRsAndValidation fundRsAndValidation = null;
        try {
            fundRsAndValidation = new FfsRsAndValidation();
            FfsData body = new FfsData();
            body.setFactSheetData("fdg;klghbdf;jbneoa;khnd'flbkndflkhnreoid;bndzfklbnoresibndlzfk[bnseriohnbodkzfvndsogb");
            fundRsAndValidation.setBody(body);
            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }


    @Test
    public void getFundFFSAndValidationFail() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        FfsRsAndValidation fundRsAndValidation = null;
        try {
            fundRsAndValidation = new FfsRsAndValidation();
            fundRsAndValidation.setError(true);
            fundRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            fundRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_MESSAGE);
            fundRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_DESC);

            when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenReturn(fundRsAndValidation);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidationError() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.BAD_REQUEST, actualResult.getStatusCode());
    }

    @Test
    public void getFundFFSAndValidationException() throws Exception {
        CustomerProfileResponseData data = new CustomerProfileResponseData();
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        when(productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FfsResponse>> actualResult = productExpServiceController
                .getFundFFSAndValidation(corrID, ffsRequestBody);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }


    @Test
    public void validateAlternativeSaleAndSwitchException() throws Exception {

        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");

        when(productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitchError() throws Exception {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");
        alternativeRq.setUnitHolderNo("PT00000000000");

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void validateAlternativeSaleAndSwitch() throws Exception {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("2");
        alternativeRq.setUnitHolderNo("PT00000000000");


        FundResponse fundRsAndValidation = null;
        try {
            fundRsAndValidation = new FundResponse();
            fundRsAndValidation.setError(false);
            fundRsAndValidation.setErrorCode("0000");
            fundRsAndValidation.setErrorDesc("success");
            fundRsAndValidation.setErrorMsg("success");

            when(productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq)).thenReturn(fundRsAndValidation);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundResponse>> actualResult = productExpServiceController
                .validateAlternativeSellAndSwitch(corrID, alternativeRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void getFundListException() throws Exception {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        when(productsExpService.getFundList(corrID, fundListRq)).thenThrow(MockitoException.class);

        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundListNotfound() throws Exception {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        try {
            when(productsExpService.getFundList(corrID, fundListRq)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }

    @Test
    public void getFundList() throws Exception {
        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);


        List<FundClassListInfo> list = new ArrayList<>();
        FundClassListInfo fundClassListInfo = new FundClassListInfo();
        fundClassListInfo.setFundCode("ABCC");
        list.add(fundClassListInfo);

        try {
            when(productsExpService.getFundList(corrID, fundListRq)).thenReturn(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> actualResult = productExpServiceController
                .getFundListInfo(corrID, fundListRq);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }


    @Test
    void testDataNotFoundError() {
        TmbOneServiceResponse<FundPaymentDetailRs> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setService("products-exp-service");
        oneServiceResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> response = productExpServiceController.dataNotFoundError(oneServiceResponse);
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
    void testGetFundPrePaymentDetail() {
        when(productsExpService.getFundPrePaymentDetail(anyString(), any())).thenReturn(null);

        ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> result = productExpServiceController.getFundPrePaymentDetail("correlationId", new FundPaymentDetailRq());
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }
}

