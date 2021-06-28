package com.tmb.oneapp.productsexpservice.feignclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class InvestmentRequestClientTest {

    @Mock
    InvestmentRequestClient investmentRequestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private final String success_code = "0000";
    private final String notfund_code = "0009";
    private AccDetailBody accDetailBody = null;
    private final FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    @Test
    public void testGetFundAccdetailInvestment() throws Exception {

        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();


        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertEquals("FFFFF", responseEntity.getBody().getData().getDetailFund().getFundHouseCode());
        Assert.assertNotNull(responseEntity.getBody().getData().getDetailFund());
    }

    @Test
    public void testGetFundAccdetailInvestmentNull() throws Exception {

        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();


        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCodeValue());
     /*
         Assert.assertEquals("FFFFF",responseEntity.getBody().getData().getDetailFund().getFundHouseCode());
        Assert.assertEquals(2,responseEntity.getBody().getData().getOrderToBeProcess().getOrder()
                 .size());
        Assert.assertNotNull(responseEntity.getBody().getData().getDetailFund());
      */
    }

}
