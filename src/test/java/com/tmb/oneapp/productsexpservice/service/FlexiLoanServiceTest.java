package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FlexiLoanServiceTest {
    @Mock
    private LendingServiceClient lendingServiceClient;

    FlexiLoanService flexiLoanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        flexiLoanService = new FlexiLoanService(lendingServiceClient);
    }

    @Test
    public void testService_creditCard() throws TMBCommonException {
        TmbOneServiceResponse<SubmissionInfoResponse> oneServiceResponse = new TmbOneServiceResponse<SubmissionInfoResponse>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        when(lendingServiceClient.submissionInfo(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("VI");
        flexiLoanService.getSubmissionInfo("001100000000000000000018593707", request);
        Assert.assertTrue(true);
    }

    @Test
    public void testService_creditCardFailed() throws TMBCommonException {
        TmbOneServiceResponse<SubmissionInfoResponse> oneServiceResponse = new TmbOneServiceResponse<SubmissionInfoResponse>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.submissionInfo(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("VI");

        assertThrows(Exception.class, () ->
                flexiLoanService.getSubmissionInfo("001100000000000000000018593707", request));
    }

    @Test
    public void testService_c2g() throws TMBCommonException {
        TmbOneServiceResponse<SubmissionInfoResponse> oneServiceResponse = new TmbOneServiceResponse<SubmissionInfoResponse>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        when(lendingServiceClient.submissionInfo(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("C2G");
        flexiLoanService.getSubmissionInfo("001100000000000000000018593707", request);
        Assert.assertTrue(true);
    }

    @Test
    public void testService_c2gFailed() throws TMBCommonException {
        TmbOneServiceResponse<SubmissionInfoResponse> oneServiceResponse = new TmbOneServiceResponse<SubmissionInfoResponse>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.submissionInfo(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        SubmissionInfoRequest request = new SubmissionInfoRequest();
        request.setCaId(1L);
        request.setProductCode("C2G");

        assertThrows(Exception.class, () ->
                flexiLoanService.getSubmissionInfo("001100000000000000000018593707", request));
    }
}
