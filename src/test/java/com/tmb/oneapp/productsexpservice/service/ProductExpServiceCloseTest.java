package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListPage;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductExpServiceCloseTest {

    InvestmentRequestClient investmentRequestClient;
    ProductsExpService productsExpService;
    AccountRequestClient accountRequestClient;
    KafkaProducerService kafkaProducerService;

    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    private final String investmentStartTime = "00:01";
    private final String investmentEndTime = "23:59";
    private final String  topicName = "activity";

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        productsExpService = new ProductsExpService(investmentRequestClient,accountRequestClient,kafkaProducerService,
                investmentStartTime, investmentEndTime, topicName);

    }

    private Map<String, String> createHeader(String correlationId){
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    @Test
    public void getFundFFSAndValidationOfShelf() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("AAAAA");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundListPage> responseList = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        Map<String, String> headers = createHeader(corrID);
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);
        FundListPage fundListPage = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            fundListPage = mapper.readValue(Paths.get("src/test/resources/investment/fund_list_info.json").toFile(), FundListPage.class);
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseList.setData(fundListPage);
            responseList.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isBusinessClose(corrID, ffsRequestBody);
        Assert.assertEquals(false, isBusClose);
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void testSaveActivityLogs() throws Exception {

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(corrID,
                ProductsExpServiceConstant.FAILED_MESSAGE, ProductsExpServiceConstant.ACTIVITY_LOG_FAILURE,
                "000000000123234445444", ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING,
                "N");

        productsExpService.logactivity(activityLogs);
        Assert.assertNotNull(activityLogs);

    }

    @Test
    public void validateTMBResponse() throws Exception {
      UtilMap utilMap = new UtilMap();
        FundAccountRs fundAccountRs = utilMap.validateTMBResponse(null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void mappingPaymentResponse() throws Exception {
        UtilMap utilMap = new UtilMap();
        FundPaymentDetailRs fundAccountRs = utilMap.mappingPaymentResponse(null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void isCASADormant() throws Exception {
        UtilMap utilMap = new UtilMap();
        boolean  fundAccountRs = utilMap.isCASADormant(null);
        Assert.assertTrue(fundAccountRs);
    }


}
