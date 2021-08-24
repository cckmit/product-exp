package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.LoanSubmissionGetCustomerAgeResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.Dropdowns;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void testGetIncomeInfoByRmIdSuccess() throws TMBCommonException {
        IncomeInfo incomeInfo = new IncomeInfo();
        incomeInfo.setIncomeAmount(BigDecimal.valueOf(100));
        incomeInfo.setStatusWorking("salary");
        TmbOneServiceResponse<IncomeInfo> oneServiceResponse = new TmbOneServiceResponse<IncomeInfo>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(incomeInfo);
        when(lendingServiceClient.getIncomeInfo(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        IncomeInfo result = loanSubmissionOnlineService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100), result.getIncomeAmount());
    }

    @Test
    public void testGetIncomeInfoByRmIdFailed() {
        TmbOneServiceResponse<IncomeInfo> oneServiceResponse = new TmbOneServiceResponse<IncomeInfo>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.getIncomeInfo(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.getIncomeInfoByRmId("crmid"));
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

    @Test
    public void testGetLoanSubmissionWorkingDetailSuccess() throws TMBCommonException {
        WorkingDetail workingDetail = new WorkingDetail();
        TmbOneServiceResponse<WorkingDetail> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(workingDetail);
        when(lendingServiceClient.getLoanSubmissionWorkingDetail(any(), any(), anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        WorkingDetail result = loanSubmissionOnlineService.getWorkingDetail("correlationId", "crmId", 1L);
        assertNotNull(result);
    }

    @Test
    public void testGetLoanSubmissionWorkingDetailFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.getLoanSubmissionWorkingDetail(any(), any(), anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.getWorkingDetail("correlationId", "crmId", 1L)
        );
    }

    @Test
    public void testGetCustomerAgeSuccess() throws TMBCommonException {
        LoanSubmissionGetCustomerAgeResponse workingDetail = new LoanSubmissionGetCustomerAgeResponse();
        TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(workingDetail);
        when(lendingServiceClient.getCustomerAge(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        LoanSubmissionGetCustomerAgeResponse result = loanSubmissionOnlineService.getCustomerAge("crmId");
        assertNotNull(result);
    }

    @Test
    public void testGetCustomerAgeFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.getCustomerAge(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.getCustomerAge("crmId")
        );
    }

}
