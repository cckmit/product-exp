package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.creditcard.ApprovalMemoCreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.facility.ApprovalMemoFacility;
import com.tmb.common.model.legacy.rsl.common.ob.checklist.Checklist;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.feature.Feature;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.application.response.Body;
import com.tmb.common.model.legacy.rsl.ws.application.response.Header;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.*;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.request.loan.UpdateWorkingDetailReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInformationResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.LoanSubmissionGetCustomerAgeResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.UpdateNCBConsentFlagRequest;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.Dropdowns;
import com.tmb.oneapp.productsexpservice.model.response.lending.dropdown.DropdownsLoanSubmissionWorkingDetail;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        ResponseApplication result = loanSubmissionOnlineService.createApplication("rmId", new LoanSubmissionCreateApplicationReq());
        assertEquals("test", result.getBody().getAppType());
    }

    @Test
    public void testCreateApplicationFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.createApplication(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.createApplication("rmId", new LoanSubmissionCreateApplicationReq()));
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


    @Test
    public void testGetPersonalDetailSuccess() throws TMBCommonException {

        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        when(lendingServiceClient.getPersonalDetail(crmid, request.getCaId())).thenReturn(ResponseEntity.ok(mockPersonalDetailResponseData()));

        PersonalDetailResponse actualResult = loanSubmissionOnlineService.getPersonalDetailInfo(crmid, request);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetPersonalDetailFailed() {

        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.getPersonalDetail(anyString(), anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.getPersonalDetailInfo(crmid, request));

    }

    private TmbOneServiceResponse<PersonalDetailResponse> mockPersonalDetailResponseData() {
        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        PersonalDetailResponse response = new PersonalDetailResponse();
        Address address = new Address();
        List<DropDown> residentList = new ArrayList<>();
        DropDown resident = new DropDown();
        address.setAmphur("แขงวังทองหลาง");
        address.setCountry("TH");
        address.setBuildingName("มบ.ปรีชา 3");
        address.setFloor("6");
        address.setMoo("2");
        address.setNo("11");
        address.setPostalCode("10400");
        address.setProvince("dm,");
        address.setRoad("ลาดพร้าว");
        address.setTumbol("ปทุมวัน");
        address.setStreetName("ลาดพร้าว");

        resident.setEntryCode("H");
        resident.setEntryId(BigDecimal.valueOf(65239));
        resident.setEntryNameEng("Mortgages");
        resident.setEntryNameTh("อยู่ระหว่างผ่อนชำระ");
        resident.setEntrySource("HOST");
        residentList.add(resident);


        response.setBirthDate(Calendar.getInstance());
        response.setEmail("kk@gmail.com");
        response.setEngName("Test");
        response.setEngSurname("Ja");
        response.setExpiryDate(Calendar.getInstance());
        response.setIdIssueCtry1("dd");
        response.setMobileNo("0987654321");
        response.setNationality("TH");
        response.setThaiName("ทีทีบี");
        response.setThaiSurname("แบงค์");
        response.setThaiSalutationCode(residentList);
        response.setAddress(address);
        response.setResidentFlag(residentList);

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(response);

        return oneServiceResponse;

    }

    @Test
    public void testSavePersonalDetailSuccess() throws TMBCommonException {
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

        when(lendingServiceClient.saveCustomerInfo("001100000000000000000018593707", personalDetailSaveInfoRequest)).thenReturn(ResponseEntity.ok(mockSavePersonalDetailResponseData()));

        PersonalDetailResponse actualResult = loanSubmissionOnlineService.updatePersonalDetailInfo("001100000000000000000018593707", personalDetailSaveInfoRequest);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testSavePersonalDetailFailed() {

        PersonalDetailSaveInfoRequest request = new PersonalDetailSaveInfoRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.saveCustomerInfo("001100000000000000000018593707", request)).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.updatePersonalDetailInfo(crmid, request));

    }

    private TmbOneServiceResponse<PersonalDetailResponse> mockSavePersonalDetailResponseData() {
        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();
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
//        ResponseIndividual response = new ResponseIndividual();
//        Header header = new Header();
//        header.setResponseCode("MSG_000");
//        response.setHeader(header);
//        response.setBody(null);

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(response);

        return oneServiceResponse;

    }


    @Test
    public void testUpdateWorkingDetailSuccess() throws TMBCommonException {

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
        when(lendingServiceClient.updateWorkingDetail(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        ResponseApplication result = loanSubmissionOnlineService.updateWorkingDetail(new UpdateWorkingDetailReq());
        assertEquals("test", result.getBody().getAppType());
    }

    @Test
    public void testUpdateWorkingDetailFailed() {
        TmbOneServiceResponse oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.updateWorkingDetail(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.updateWorkingDetail(new UpdateWorkingDetailReq()));
    }


    @Test
    void testUpdateNCBConsentFlagAndStoreFile() throws Exception {

        CustomerInformationResponse customerInfoRes = new CustomerInformationResponse();
        TmbOneServiceResponse<CustomerInformationResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(customerInfoRes);
        when(lendingServiceClient.updateNCBConsentFlagAndStoreFile(any(), any(), any()))
                .thenReturn(ResponseEntity.ok(oneServiceResponse));

        Assert.assertNotNull(loanSubmissionOnlineService
                .updateNCBConsentFlagAndStoreFile("correlationId", "crmid", new UpdateNCBConsentFlagRequest()));
    }


    @Test
    public void testGetChecklistDocumentSuccess() throws TMBCommonException {

        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        when(lendingServiceClient.getDocuments(crmid, request.getCaId())).thenReturn(ResponseEntity.ok(mockChecklistResponseData()));

        List<ChecklistResponse> actualResult = loanSubmissionOnlineService.getDocuments(crmid, request.getCaId());

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetChecklistDocumentFailed() {

        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<List<ChecklistResponse>> oneServiceResponse = new TmbOneServiceResponse<List<ChecklistResponse>>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.getDocuments(anyString(), anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.getDocuments(crmid, request.getCaId()));

    }

    private TmbOneServiceResponse<List<ChecklistResponse>> mockChecklistResponseData() {
        TmbOneServiceResponse<List<ChecklistResponse>> oneServiceResponse = new TmbOneServiceResponse<List<ChecklistResponse>>();
        ChecklistResponse responseChecklist = new ChecklistResponse();
        List<ChecklistResponse> checklistResponse = new ArrayList<>();
        com.tmb.common.model.legacy.rsl.ws.checklist.response.Body body = new com.tmb.common.model.legacy.rsl.ws.checklist.response.Body();
        com.tmb.common.model.legacy.rsl.ws.checklist.response.Header header = new com.tmb.common.model.legacy.rsl.ws.checklist.response.Header();
        header.setResponseCode("MSG_000");
        Checklist checklist = new Checklist();
        Checklist[] checklists = new Checklist[1];
        responseChecklist.setChecklistType("CC");
        responseChecklist.setCifRelCode("M");
        responseChecklist.setStatus("ACTIVE");
        responseChecklist.setDocDescription("xx");
        responseChecklist.setDocId(BigDecimal.ONE);
        responseChecklist.setDocumentCode("ID01");
        responseChecklist.setIncompletedDocReasonCd("xx");
        responseChecklist.setIncompletedDocReasonDesc("xx");
        responseChecklist.setId(BigDecimal.ONE);
        responseChecklist.setIsMandatory("Y");
        responseChecklist.setLosCifId(BigDecimal.ONE);

        checklist.setChecklistType("CC");
        checklist.setCifRelCode("M");
        checklist.setStatus("ACTIVE");
        checklist.setDocDescription("xx");
        checklist.setDocId(BigDecimal.ONE);
        checklist.setDocumentCode("ID01");
        checklist.setIncompletedDocReasonCd("xx");
        checklist.setIncompletedDocReasonDesc("xx");
        checklist.setId(BigDecimal.ONE);
        checklist.setIsMandatory("Y");
        checklist.setLosCifId(BigDecimal.ONE);
        checklists[0] = checklist;
        body.setCustomerChecklists(checklists);

        checklistResponse.add(responseChecklist);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(checklistResponse);
        return oneServiceResponse;
    }

    @Test
    void testGetCustomerInfoAndApplicationInfo() throws Exception {
        CustomerInformationResponse customerInfoRes = new CustomerInformationResponse();
        TmbOneServiceResponse<CustomerInformationResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(customerInfoRes);
        when(lendingServiceClient.getCustomerInformation(any(), any(), any()))
                .thenReturn(ResponseEntity.ok(oneServiceResponse));

        Assert.assertNotNull(loanSubmissionOnlineService.getCustomerInformation("correlationId",
                "crmid", new UpdateNCBConsentFlagRequest()));
    }


    @Test
    public void testCheckCalculateUnderwritingApprove() throws TMBCommonException {

        InstantLoanCalUWRequest instantLoanCalUWRequest = new InstantLoanCalUWRequest();
        instantLoanCalUWRequest.setCaId(BigDecimal.TEN);
        instantLoanCalUWRequest.setProduct("RC01");
        instantLoanCalUWRequest.setTriggerFlag("Y");

        when(lendingServiceClient.checkApprovedStatus(instantLoanCalUWRequest.getCaId(),instantLoanCalUWRequest.getTriggerFlag(),instantLoanCalUWRequest.getProduct())).thenReturn(ResponseEntity.ok(mockCalUW()));

        InstantLoanCalUWResponse actualResult = loanSubmissionOnlineService.checkCalculateUnderwriting(instantLoanCalUWRequest);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testCheckCalculateUnderwritingApproveFailed() {
        InstantLoanCalUWRequest instantLoanCalUWRequest = new InstantLoanCalUWRequest();
        instantLoanCalUWRequest.setCaId(BigDecimal.TEN);
        instantLoanCalUWRequest.setProduct("RC01");
        instantLoanCalUWRequest.setTriggerFlag("Y");

        TmbOneServiceResponse<InstantLoanCalUWResponse> oneServiceResponse = new TmbOneServiceResponse<InstantLoanCalUWResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.checkApprovedStatus(any(),any(),any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.checkCalculateUnderwriting(instantLoanCalUWRequest));

    }

    @Test
    public void testCheckCalculateUnderwritingApproveC2G() throws TMBCommonException {

        InstantLoanCalUWRequest calUWReq = new InstantLoanCalUWRequest();
        calUWReq.setProduct("C2G");
        calUWReq.setTriggerFlag("Y");
        calUWReq.setCaId(BigDecimal.TEN);

        when(lendingServiceClient.checkApprovedStatus(any(),any(),any())).thenReturn(ResponseEntity.ok(mockCalUW()));

        InstantLoanCalUWResponse actualResult = loanSubmissionOnlineService.checkCalculateUnderwriting(calUWReq);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testCheckCalculateUnderwritingApproveC2GFailed() {

        InstantLoanCalUWRequest calUWReq = new InstantLoanCalUWRequest();
        calUWReq.setProduct("C2G");
        calUWReq.setTriggerFlag("Y");
        calUWReq.setCaId(BigDecimal.TEN);

        TmbOneServiceResponse<InstantLoanCalUWResponse> oneServiceResponse = new TmbOneServiceResponse<InstantLoanCalUWResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.checkApprovedStatus(any(),any(),any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                loanSubmissionOnlineService.checkCalculateUnderwriting(calUWReq));

    }

    private TmbOneServiceResponse<InstantLoanCalUWResponse> mockCalUW() {
        TmbOneServiceResponse<InstantLoanCalUWResponse> oneServiceResponse = new TmbOneServiceResponse<InstantLoanCalUWResponse>();

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        LoanCustomerPricing pricing = new LoanCustomerPricing();
        pricing.setMonthTo(BigDecimal.ONE);
        pricing.setMonthFrom(BigDecimal.ONE);
        pricing.setRateVariance(BigDecimal.ONE);
        pricing.setRate("1");
        pricingList.add(pricing);

        InstantLoanCalUWResponse instantLoanCalUWResponse = new InstantLoanCalUWResponse();
        instantLoanCalUWResponse.setTenor(BigDecimal.ONE);
        instantLoanCalUWResponse.setRequestAmount(BigDecimal.TEN);
        instantLoanCalUWResponse.setStatus("S");
        instantLoanCalUWResponse.setLoanContractDate(Calendar.getInstance());
        instantLoanCalUWResponse.setInstallmentAmount(BigDecimal.ONE);
        instantLoanCalUWResponse.setProduct("RC01");
        instantLoanCalUWResponse.setPayDate("25");
        instantLoanCalUWResponse.setCreditLimit(BigDecimal.TEN);
        instantLoanCalUWResponse.setFirstPaymentDueDate("25");
        instantLoanCalUWResponse.setOutStandingBalance(BigDecimal.TEN);
        instantLoanCalUWResponse.setRateTypePercent(BigDecimal.TEN);
        instantLoanCalUWResponse.setDisburstAccountNo("xxx");
        instantLoanCalUWResponse.setInterestRate(BigDecimal.ONE);
        instantLoanCalUWResponse.setPricings(pricingList);

        ResponseInstantLoanCalUW response = new ResponseInstantLoanCalUW();
        ApprovalMemoCreditCard[] approvalMemoCreditCardList = new ApprovalMemoCreditCard[10];
        ApprovalMemoCreditCard approvalMemoCreditCard = new ApprovalMemoCreditCard();
        approvalMemoCreditCard.setCardType("T");
        approvalMemoCreditCard.setCreditLimit(BigDecimal.ONE);
        approvalMemoCreditCard.setUnderwritingResult("APPROVE");
        approvalMemoCreditCard.setCcId(BigDecimal.TEN);
        approvalMemoCreditCard.setCycleCutDate("20/05/2021");
        approvalMemoCreditCard.setDebitAccountName("ttb");
        approvalMemoCreditCard.setDebitAccountNo("111");
        approvalMemoCreditCard.setFirstPaymentDueDate("21/05/21");
        approvalMemoCreditCard.setId(BigDecimal.ONE);
        approvalMemoCreditCard.setPaymentMethod("f");
        approvalMemoCreditCard.setPayDate("22/05/21");
        approvalMemoCreditCardList[0] = approvalMemoCreditCard;

        ApprovalMemoFacility[] approvalMemoFacilities = new ApprovalMemoFacility[5];
        ApprovalMemoFacility approvalMemoFacility = new ApprovalMemoFacility();
        approvalMemoFacility.setTenor(BigDecimal.TEN);
        approvalMemoFacility.setCreditLimit(BigDecimal.ONE);
        approvalMemoFacility.setCycleCutDate("11/11/11");
        approvalMemoFacility.setId(BigDecimal.ONE);
        approvalMemoFacility.setUnderwritingResult("APPROVE");
        approvalMemoFacility.setDisburstAccountName("ttb");
        approvalMemoFacility.setDisburstAccountNo("11");
        approvalMemoFacility.setFacId(BigDecimal.TEN);
        approvalMemoFacility.setTenor(BigDecimal.TEN);
        approvalMemoFacility.setPayDate("25");
        approvalMemoFacility.setFirstPaymentDueDate("11/11/11");
        approvalMemoFacility.setLoanContractDate(Calendar.getInstance());
        approvalMemoFacility.setInstallmentAmount(BigDecimal.TEN);
        approvalMemoFacility.setInterestRate(BigDecimal.TEN);
        approvalMemoFacility.setOutstandingBalance(BigDecimal.TEN);
        approvalMemoFacility.setRateTypePercent(BigDecimal.TEN);
        approvalMemoFacility.setRateType("Y");
        approvalMemoFacility.setDisburstAccountName("ttb");
        approvalMemoFacility.setDisburstAccountNo("11");
        approvalMemoFacility.setOutstandingBalance(BigDecimal.TEN);
        approvalMemoFacilities[0] = approvalMemoFacility;

        com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body responseBody = new com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body();
        responseBody.setUnderwritingResult("APPROVE");
        responseBody.setApprovalMemoCreditCards(approvalMemoCreditCardList);
        responseBody.setApprovalMemoFacilities(approvalMemoFacilities);
        response.setBody(responseBody);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(instantLoanCalUWResponse);
        return oneServiceResponse;
    }

    private ResponseFacility mockFacility() {
        ResponseFacility responseFacility = new ResponseFacility();
        Facility f = new Facility();
        Feature feature = new Feature();
        com.tmb.common.model.legacy.rsl.ws.facility.response.Body body = new com.tmb.common.model.legacy.rsl.ws.facility.response.Body();
        f.setCardDelivery("H");
        f.setCaId(BigDecimal.TEN);
        f.setFeatureType("S");
        f.setCaCampaignCode("U");
        f.setAmountFinance(BigDecimal.TEN);
        f.setDisburstBankName("ttb");
        f.setDisburstAccountName("ttb");
        f.setDisburstAccountNo("111");
        f.setOutStandingBalance(BigDecimal.TEN);
        f.setConsiderLoanWithOtherBank("bkk");
        f.setCreditLimitFromMof(BigDecimal.TEN);
        f.setExistingAccountNo("111");
        f.setExistingCreditLimit(BigDecimal.TEN);
        f.setExistLoan("aaa");
        f.setPricings(mockPricing());
        feature.setRequestAmount(BigDecimal.TEN);
        f.setFeature(feature);

        Facility[] facilitys = new Facility[1];
        facilitys[0] = f;
        body.setFacilities(facilitys);
        responseFacility.setBody(body);
        return responseFacility;
    }

    private Pricing[] mockPricing() {
        InstantLoanCalUWResponse instantLoanCalUWResponse = new InstantLoanCalUWResponse();
        Pricing[] pricings = new Pricing[1];
        Pricing p = new Pricing();
        p.setMonthFrom(BigDecimal.ONE);
        p.setMonthTo(BigDecimal.ONE);
        p.setPercentSign("S");
        p.setPricingType("");
        p.setRateType("S");
        p.setRateVaraince(BigDecimal.ONE);
        pricings[0] = p;

        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        pricings[0].setMonthTo(BigDecimal.ONE);
        pricings[0].setMonthFrom(BigDecimal.ONE);
        pricings[0].setRateVaraince(BigDecimal.ONE);

        LoanCustomerPricing pricing = new LoanCustomerPricing();
        pricing.setMonthTo(pricings[0].getMonthTo());
        pricing.setMonthFrom(pricings[0].getMonthFrom());
        pricing.setRateVariance(pricings[0].getRateVaraince());
        pricing.setRate("1");
        pricingList.add(pricing);
        instantLoanCalUWResponse.setPricings(pricingList);
        return pricings;
    }

}
