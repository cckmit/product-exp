package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanStatementResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class LoanStatementControllerTest {
    @Mock
    TMBLogger<LoanStatementController> log;
    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    LoanStatementController loanStatementController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanStatementController = new LoanStatementController(accountRequestClient);
    }

    @Test
    public void testGetLoanAccountDetail()  {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");

        TmbOneServiceResponse<LoanStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setDescription("working");
        status.setCode("0");
        status.setMessage("Successful");
        status.setService("loan-statement-get");
        oneServiceResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> response = new ResponseEntity<>(oneServiceResponse, HttpStatus.OK);
        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(response);

        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanAccountDetail(reqHeaders, requestBody);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }
    public Map<String, String> headerRequestParameter(String correlationId) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        return reqData;

    }
}

