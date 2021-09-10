package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.Body;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.Header;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.personaldetail.*;
import com.tmb.oneapp.productsexpservice.model.request.lending.EAppRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateApplicationRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.*;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionOnlineService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class LoanSubmissionOnlineControllerTest {

    @InjectMocks
    LoanSubmissionOnlineController loanSubmissionOnlineController;

    @Mock
    LoanSubmissionOnlineService loanSubmissionOnlineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetIncomeInfoByRmIdSuccess() throws TMBCommonException {
        IncomeInfo res = new IncomeInfo();
        res.setIncomeAmount(BigDecimal.valueOf(100));
        when(loanSubmissionOnlineService.getIncomeInfoByRmId(any())).thenReturn(res);
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetIncomeInfoByRmIdFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.getIncomeInfoByRmId(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testCreateApplicationSuccess() throws TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(loanSubmissionOnlineService.createApplication(any(), any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid", new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCreateApplicationFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.createApplication(any(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid", new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().isError());
    }


    @Test
    public void testGetEAppSuccess() throws TMBCommonException {
        EAppRequest request = new EAppRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        EAppResponse response = new EAppResponse();
        when(loanSubmissionOnlineService.getEAppData(anyString(),any(),anyLong())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<EAppResponse>> responseEntity = loanSubmissionOnlineController.getEAppData(correlationId,crmid,request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void tesGetEAppFail() throws TMBCommonException {
        EAppRequest request = new EAppRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        when(loanSubmissionOnlineService.getEAppData(anyString(),any(),anyLong())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<EAppResponse>> responseEntity = loanSubmissionOnlineController.getEAppData(correlationId,crmid,request);
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
    public void testGetLoanSubmissionWorkingDetailSuccess() throws  TMBCommonException {
        WorkingDetail workingDetail = new WorkingDetail();
        when(loanSubmissionOnlineService.getWorkingDetail(any(),any(), anyLong())).thenReturn(workingDetail);
        ResponseEntity<TmbOneServiceResponse<WorkingDetail>> responseEntity = loanSubmissionOnlineController.getWorkingDetail("correlationId", "crmid", 1L);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetLoanSubmissionWorkingDetailFail() throws  TMBCommonException {
        when(loanSubmissionOnlineService.getWorkingDetail(any(),any(), anyLong())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<WorkingDetail>> responseEntity = loanSubmissionOnlineController.getWorkingDetail("correlationId", "crmid", 1L);
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testUpdateWorkingDetailSuccess() throws TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(loanSubmissionOnlineService.updateWorkingDetail(any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.updateWorkingDetail(new UpdateWorkingDetailReq());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdateWorkingDetailFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.updateWorkingDetail(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.updateWorkingDetail(new UpdateWorkingDetailReq());
        assertTrue(responseEntity.getStatusCode().isError());
    }
    
	@Test
	public void testGetcustomerInfoResSuccess() throws TMBCommonException {
		CustomerInformationResponse customerInfoRes = new CustomerInformationResponse();
		when(loanSubmissionOnlineService.getCustomerInformation(any(), any(), any()))
				.thenReturn(customerInfoRes);
		ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> responseEntity = loanSubmissionOnlineController
				.getCustomerInformation("correlationId", "crmid", new UpdateNCBConsentFlagRequest());
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void testGetcustomerInfoResFail() throws TMBCommonException {
		when(loanSubmissionOnlineService.getCustomerInformation(any(), any(), any()))
				.thenThrow(new IllegalArgumentException());
		ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> responseEntity = loanSubmissionOnlineController
				.getCustomerInformation("correlationId", "crmid",  new UpdateNCBConsentFlagRequest());
		assertTrue(responseEntity.getStatusCode().isError());
	}
	
	@Test
	public void testUpdateNCBConsentFlagAndStoreFileResSuccess() throws TMBCommonException {
		CustomerInformationResponse customerInfoRes = new CustomerInformationResponse();
		when(loanSubmissionOnlineService.updateNCBConsentFlagAndStoreFile(any(), any(), any()))
				.thenReturn(customerInfoRes);
		ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> responseEntity = loanSubmissionOnlineController
				.updateNCBConsentFlagAndStoreFile("correlationId", "crmid", new UpdateNCBConsentFlagRequest());
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void testUpdateNCBConsentFlagAndStoreFileResFail() throws TMBCommonException {
		when(loanSubmissionOnlineService.updateNCBConsentFlagAndStoreFile(any(), any(), any()))
				.thenThrow(new IllegalArgumentException());
		ResponseEntity<TmbOneServiceResponse<CustomerInformationResponse>> responseEntity = loanSubmissionOnlineController
				.updateNCBConsentFlagAndStoreFile("correlationId", "crmid",  new UpdateNCBConsentFlagRequest());
		assertTrue(responseEntity.getStatusCode().isError());
	}

    @Test
    public void testGetCustomerAgeSuccess() throws TMBCommonException {
        when(loanSubmissionOnlineService.getCustomerAge(any())).thenReturn(new LoanSubmissionGetCustomerAgeResponse());
        ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> responseEntity = loanSubmissionOnlineController.getCustomerAge("rmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetCustomerAgeFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.getCustomerAge(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanSubmissionGetCustomerAgeResponse>> responseEntity = loanSubmissionOnlineController.getCustomerAge("rmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }


    @Test
    public void testGetPersonalDetailInfoSuccess() throws TMBCommonException {
        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        PersonalDetailResponse response = new PersonalDetailResponse();
        when(loanSubmissionOnlineService.getPersonalDetailInfo(any(),any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> responseEntity = loanSubmissionOnlineController.getPersonalDetail(correlationId, request);
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetPersonalDetailInfoFail() throws  TMBCommonException {
        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(1L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        when(loanSubmissionOnlineService.getPersonalDetailInfo(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> responseEntity = loanSubmissionOnlineController.getPersonalDetail(correlationId, request);
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testUpdateApplicationSuccess() throws TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(loanSubmissionOnlineService.updateApplication(anyString(),any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse> responseEntity = loanSubmissionOnlineController.updateApplication("crmId", new UpdateApplicationRequest());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdateApplicationFail() throws TMBCommonException {
        when(loanSubmissionOnlineService.updateApplication(anyString(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse> responseEntity = loanSubmissionOnlineController.updateApplication("crmId",new UpdateApplicationRequest());
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testSavePersonalDetailInfoSuccess() throws TMBCommonException {
        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);

        PersonalDetailSaveInfoRequest personalDetailSaveInfoRequest = new PersonalDetailSaveInfoRequest();
        com.tmb.oneapp.productsexpservice.model.personaldetail.Address address = new com.tmb.oneapp.productsexpservice.model.personaldetail.Address();
        DropDown resident = new DropDown();
        address.setCountry("TH");
        address.setNo("111");
        address.setRoomNo("1111");
        address.setFloor("6");
        address.setBuildingName("xx");
        address.setProvince("xx");
        address.setMoo("1");
        address.setPostalCode("122222");
        address.setStreetName("xx");
        address.setRoad("xx");
        address.setTumbol("xx");
        address.setAmphur("xx");

        resident.setEntrySource("111");
        resident.setEntryId(BigDecimal.ONE);
        resident.setEntryCode("xx");
        resident.setEntryNameTh("xx");
        resident.setEntryNameEng("xx");

        personalDetailSaveInfoRequest.setThaiSalutationCode("xx");
        personalDetailSaveInfoRequest.setEngName("xx");
        personalDetailSaveInfoRequest.setEngSurname("xx");
        personalDetailSaveInfoRequest.setThaiName("xx");
        personalDetailSaveInfoRequest.setThaiSurname("xx");
        personalDetailSaveInfoRequest.setEmail("xx");
        personalDetailSaveInfoRequest.setBirthDate(Calendar.getInstance());
        personalDetailSaveInfoRequest.setIdIssueCtry1("xx");
        personalDetailSaveInfoRequest.setExpiryDate(Calendar.getInstance());
        personalDetailSaveInfoRequest.setNationality("xx");
        personalDetailSaveInfoRequest.setAddress(address);
        personalDetailSaveInfoRequest.setMobileNo("xx");
        personalDetailSaveInfoRequest.setResidentFlag(resident.getEntryCode());

        when(loanSubmissionOnlineService.updatePersonalDetailInfo(any(),any())).thenReturn(mockResponseIndividual().getData());
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> result = loanSubmissionOnlineController.savePersonalDetail("001100000000000000000018593707",personalDetailSaveInfoRequest);
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }

    private TmbOneServiceResponse<PersonalDetailResponse> mockResponseIndividual() {

        PersonalDetailResponse response = new PersonalDetailResponse();

        Address address1 = new Address();
        address1.setNo("111");
        address1.setRoad("xx");
        address1.setCountry("TH");
        address1.setFloor("6");
        address1.setTumbol("xxx");
        address1.setMoo("2");
        address1.setStreetName("xxx");
        address1.setProvince("xxx");
        address1.setPostalCode("10400");
        address1.setBuildingName("xx");
        address1.setAmphur("xxx");

        DropDown resident = new DropDown();
        resident.setEntryNameEng("xxx");
        resident.setEntryNameTh("xxx");
        resident.setEntryCode("xx");
        resident.setEntryId(BigDecimal.ONE);
        resident.setEntrySource("H");

        response.setPrefix("G01");
        response.setEngSurname("xxx");
        response.setCitizenId("1111");
        response.setIdIssueCtry1("111");
        response.setAddress(address1);
        response.setResidentFlag(Collections.singletonList(resident));

        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();
        Body body = new Body();
        Header header = new Header();
//
//        ResponseIndividual response = new ResponseIndividual();
//
//        response.setBody(body);
//        response.setHeader(header);

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        oneServiceResponse.setData(response);

        return oneServiceResponse;
    }

    @Test
    public void testGetChecklistSuccess() throws TMBCommonException {
        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        List<ChecklistResponse> response = new ArrayList<>();
        when(loanSubmissionOnlineService.getDocuments(any(),any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> responseEntity = loanSubmissionOnlineController.getDocuments(correlationId, request);
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetChecklistInfoFail() throws  TMBCommonException {
        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(1L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        when(loanSubmissionOnlineService.getDocuments(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> responseEntity = loanSubmissionOnlineController.getDocuments(correlationId, request);
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

}