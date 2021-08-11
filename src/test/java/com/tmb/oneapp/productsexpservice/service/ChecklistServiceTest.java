package com.tmb.oneapp.productsexpservice.service;


import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.common.ob.checklist.Checklist;
import com.tmb.common.model.legacy.rsl.ws.checklist.response.Body;
import com.tmb.common.model.legacy.rsl.ws.checklist.response.Header;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ChecklistServiceTest {
    @Mock
    private LendingServiceClient lendingServiceClient;

    ChecklistService checklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        checklistService = new ChecklistService(lendingServiceClient);
    }

    @Test
    public void testGetChecklistDocumentSuccess() throws TMBCommonException {

        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        when(lendingServiceClient.getDocuments(crmid,request.getCaId())).thenReturn(ResponseEntity.ok(mockChecklistResponseData()));

        List<ChecklistResponse> actualResult = checklistService.getDocuments(crmid,request);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetChecklistDocumentFailed() {

        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<List<ChecklistResponse>> oneServiceResponse = new TmbOneServiceResponse<List<ChecklistResponse>>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.getDocuments(anyString(),anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                checklistService.getDocuments(crmid,request));

    }

    private TmbOneServiceResponse<List<ChecklistResponse>> mockChecklistResponseData() {
        TmbOneServiceResponse<List<ChecklistResponse>> oneServiceResponse = new TmbOneServiceResponse<List<ChecklistResponse>>();
        ChecklistResponse responseChecklist = new ChecklistResponse();
        List<ChecklistResponse> checklistResponse = new ArrayList<>();
        com.tmb.common.model.legacy.rsl.ws.checklist.response.Body body = new Body();
        Header header = new Header();
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
}