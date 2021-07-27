package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.service.PersonalDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalDetailControllerTest {

    @Mock
    PersonalDetailService personalDetailService;

    PersonalDetailController personalDetailController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        personalDetailController = new PersonalDetailController(personalDetailService);
    }

    @Test
    public void testGetPersonalDetailInfoSuccess() throws TMBCommonException {
        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        PersonalDetailResponse response = new PersonalDetailResponse();
        when(personalDetailService.getPersonalDetailInfo(any(),any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> responseEntity = personalDetailController.getPersonalDetail(correlationId, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetPersonalDetailInfoFail() throws  TMBCommonException {
        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(1L);
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
//        PersonalDetailResponse response = new PersonalDetailResponse();
        when(personalDetailService.getPersonalDetailInfo(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> responseEntity = personalDetailController.getPersonalDetail(correlationId, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

}