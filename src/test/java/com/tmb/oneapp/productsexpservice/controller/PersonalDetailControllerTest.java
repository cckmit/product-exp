package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.Body;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.Header;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.personaldetail.*;
import com.tmb.oneapp.productsexpservice.service.PersonalDetailSaveInfoService;
import com.tmb.oneapp.productsexpservice.service.PersonalDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalDetailControllerTest {

    @Mock
    PersonalDetailService personalDetailService;

    @Mock
    PersonalDetailSaveInfoService personalDetailSaveInfoService;

    PersonalDetailController personalDetailController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        personalDetailController = new PersonalDetailController(personalDetailService,personalDetailSaveInfoService);
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
        when(personalDetailService.getPersonalDetailInfo(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> responseEntity = personalDetailController.getPersonalDetail(correlationId, request);
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
        personalDetailSaveInfoRequest.setEngSurName("xx");
        personalDetailSaveInfoRequest.setThaiName("xx");
        personalDetailSaveInfoRequest.setThaiSurName("xx");
        personalDetailSaveInfoRequest.setEmail("xx");
        personalDetailSaveInfoRequest.setBirthDate(Calendar.getInstance());
        personalDetailSaveInfoRequest.setIdIssueCtry1("xx");
        personalDetailSaveInfoRequest.setExpiryDate(Calendar.getInstance());
        personalDetailSaveInfoRequest.setNationality("xx");
        personalDetailSaveInfoRequest.setAddress(address);
        personalDetailSaveInfoRequest.setMobileNo("xx");
        personalDetailSaveInfoRequest.setResidentFlag(resident.getEntryCode());

        when(personalDetailSaveInfoService.updatePersonalDetailInfo(any(),any())).thenReturn(mockResponseIndividual().getData());
        ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> result = personalDetailController.savePersonalDetail("001100000000000000000018593707",personalDetailSaveInfoRequest);
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

}