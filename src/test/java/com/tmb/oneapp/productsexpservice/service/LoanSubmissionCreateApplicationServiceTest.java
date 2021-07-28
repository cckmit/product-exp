package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.application.response.Body;
import com.tmb.common.model.legacy.rsl.ws.application.response.Header;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanSubmissionCreateApplicationServiceTest {

    @Mock
    private LendingServiceClient lendingServiceClient;

    LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionCreateApplicationService = new LoanSubmissionCreateApplicationService(lendingServiceClient);
    }

    @Test
    public void testCreateApplicationSuccess() throws TMBCommonException {

        Header header = new Header();
        header.setResponseCode("MSG_000");
        Body body = new Body();
        body.setAppType("test");
        ResponseApplication responseApplication = new ResponseApplication();
        responseApplication.setHeader(header);
        responseApplication.setBody(body);
        TmbOneServiceResponse<ResponseApplication> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(responseApplication);
        when(lendingServiceClient.createApplication(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        ResponseApplication result = loanSubmissionCreateApplicationService.createApplication("rmId", new LoanSubmissionCreateApplicationReq());
        assertEquals(result.getBody().getAppType(), "test");
    }

    @Test
    public void testCreateApplicationFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.createApplication(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionCreateApplicationService.createApplication("rmId", new LoanSubmissionCreateApplicationReq()));
    }

}