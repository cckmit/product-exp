package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class LoanCalculatorServiceTest {

    @Mock
    private LendingServiceClient lendingServiceClient;

    @InjectMocks
    LoanCalculatorService loanCalculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPreloadLoanCalSuccess() throws TMBCommonException {

        LoanCalculatorRequest request = new LoanCalculatorRequest();
        request.setCaId(2021071404188196L);
        request.setProduct("RC");

        when(lendingServiceClient.getPreloadLoanCalculator(anyLong(),anyString())).thenReturn(ResponseEntity.ok(mockData()));

        LoanCalculatorResponse actualResult = loanCalculatorService.getPreloadLoanCal(request);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetPreloadLoanCalFailed() {

        LoanCalculatorRequest request = new LoanCalculatorRequest();
        request.setCaId(2021071404188196L);
        request.setProduct("RC");

        TmbOneServiceResponse<LoanCalculatorResponse> oneServiceResponse = new TmbOneServiceResponse<LoanCalculatorResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.getPreloadLoanCalculator(anyLong(), anyString())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanCalculatorService.getPreloadLoanCal(request));

    }

    private TmbOneServiceResponse<LoanCalculatorResponse> mockData() {
        TmbOneServiceResponse<LoanCalculatorResponse> oneServiceResponse = new TmbOneServiceResponse<LoanCalculatorResponse>();
        LoanCalculatorResponse response = new LoanCalculatorResponse();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(response);
        return oneServiceResponse;
    }
}