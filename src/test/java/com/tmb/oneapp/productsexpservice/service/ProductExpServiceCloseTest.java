package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductExpServiceCloseTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ProductsExpService productsExpService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000000028365";

    @Test
    public void testSaveActivityLogs() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setUnitHolderNumber("PT000000000000587870");

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    void testCreateLogWithException() throws Exception {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setUnitHolderNumber("PT000000000000587870");

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);
        doNothing().when(kafkaProducerService).sendMessageAsync(anyString(), any());
        when(mapper.writeValueAsString(anyString())).thenThrow(MockitoException.class);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    public void testSaveActivityLogsNullUnit() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_FAILURE,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    public void validateTMBResponse() {
        FundAccountResponse fundAccountResponse = UtilMap.validateTMBResponse(null, null, null);
        Assert.assertNull(fundAccountResponse);
    }

    @Test
    public void mappingPaymentResponse() {
        UtilMap utilMap = new UtilMap();
        FundPaymentDetailResponse fundAccountRs = utilMap.mappingPaymentResponse(null, null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void convertAccountType() {
        String fundAccountRs = UtilMap.convertAccountType("AAAA");
        Assert.assertEquals("", fundAccountRs);
    }

    @Test
    public void isCASADormantException() {
        boolean fundAccountRs = UtilMap.isCASADormant("data not found");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void isCASADormant() {
        boolean fundAccountRs = UtilMap.isCASADormant(null);
        Assert.assertTrue(fundAccountRs);
    }

    @Test
    public void isBusinessCloseException() {
        boolean fundAccountRs = UtilMap.isBusinessClose("06:00", "08:00");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void addColonDateFormat() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("06:00");
        Assert.assertEquals("0600", fundAccountRs);
    }

    @Test
    public void addColonDateFormatFail() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("");
        Assert.assertEquals("", fundAccountRs);
    }
}
