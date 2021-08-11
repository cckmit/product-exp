package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCreateApplicationService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionIncomeInfoService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import com.tmb.oneapp.productsexpservice.service.WorkingDetailUpdateInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanSubmissionOnlineControllerTest {

    @InjectMocks
    LoanSubmissionOnlineController loanSubmissionOnlineController;

    @Mock
    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @Mock
    LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;

    @Mock
    LoanSubmissionOnlineService loanSubmissionOnlineService;

    @Mock
    WorkingDetailUpdateInfoService workingDetailUpdateInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetIncomeInfoByRmIdSuccess() throws TMBCommonException {
        IncomeInfo res = new IncomeInfo();
        res.setIncomeAmount(BigDecimal.valueOf(100));
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenReturn(res);
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetIncomeInfoByRmIdFail() throws TMBCommonException {
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testCreateApplicationSuccess() throws TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(loanSubmissionCreateApplicationService.createApplication(any(), any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid", new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCreateApplicationFail() throws TMBCommonException {
        when(loanSubmissionCreateApplicationService.createApplication(any(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid", new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testGetDropdownLoanSubmissionWorkingDetailSuccess() throws TMBCommonException {
        DropdownsLoanSubmissionWorkingDetail dropdownWorkingDetail = new DropdownsLoanSubmissionWorkingDetail();
        when(loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail(any(), any())).thenReturn(dropdownWorkingDetail);
        ResponseEntity<TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail>> responseEntity = loanSubmissionOnlineController.getDropdownLoanSubmissionWorkingDetail("correlationId", "crmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetDropdownLoanSubmissionWorkingDetailFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.getDropdownsLoanSubmissionWorkingDetail(any(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<DropdownsLoanSubmissionWorkingDetail>> responseEntity = loanSubmissionOnlineController.getDropdownLoanSubmissionWorkingDetail("correlationId", "crmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }


    @Test
    public void testUpdateWorkingDetailSuccess() throws TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(workingDetailUpdateInfoService.updateWorkingDetail(any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.updateWorkingDetail(new UpdateWorkingDetailReq());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdateWorkingDetailFail() throws TMBCommonException {
        when(workingDetailUpdateInfoService.updateWorkingDetail(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.updateWorkingDetail(new UpdateWorkingDetailReq());
        assertTrue(responseEntity.getStatusCode().isError());
    }

}