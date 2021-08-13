package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistResponse;
import com.tmb.oneapp.productsexpservice.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ChecklistControllerTest {
    @Mock
    ChecklistService checklistService;

    ChecklistController checklistController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        checklistController = new ChecklistController(checklistService);
    }

    @Test
    public void testGetChecklistSuccess() throws TMBCommonException {
        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(2021071404188196L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        List<ChecklistResponse> response = new ArrayList<>();
        when(checklistService.getDocuments(any(),any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> responseEntity = checklistController.getDocuments(correlationId, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetChecklistInfoFail() throws  TMBCommonException {
        ChecklistRequest request = new ChecklistRequest();
        request.setCaId(1L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        when(checklistService.getDocuments(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> responseEntity = checklistController.getDocuments(correlationId, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

}