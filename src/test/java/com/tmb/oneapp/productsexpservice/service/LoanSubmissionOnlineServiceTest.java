package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.application.response.Body;
import com.tmb.common.model.legacy.rsl.ws.application.response.Header;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.constant.RslResponseCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.Dropdowns;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.when;

class LoanSubmissionOnlineServiceTest {

    @Mock
    private LendingServiceClient lendingServiceClient;

    @InjectMocks
    LoanSubmissionOnlineService loanSubmissionOnlineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDropdownsLoanSubmissionWorkingDetailSuccess() throws TMBCommonException {
        DropdownsLoanSubmissionWorkingDetail dropdownsWorkingDetail = new DropdownsLoanSubmissionWorkingDetail();
        dropdownsWorkingDetail.setEmploymentStatus(List.of(Dropdowns.EmploymentStatus.builder().build()));
        TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(dropdownsWorkingDetail);
        when(lendingServiceClient.getDropdownLoanSubmissionWorkingDetail(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        DropdownsLoanSubmissionWorkingDetail result = loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail("correlationId", "crmId");
        assertEquals(dropdownsWorkingDetail.getEmploymentStatus(), result.getEmploymentStatus());
    }

    @Test
    public void testGetDropdownsLoanSubmissionWorkingDetailFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.getDropdownLoanSubmissionWorkingDetail(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
              loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail("correlationId", "crmId")
        );
    }

}