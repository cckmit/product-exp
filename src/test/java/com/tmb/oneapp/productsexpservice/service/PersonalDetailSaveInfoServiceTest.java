package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.Address;
import com.tmb.oneapp.productsexpservice.model.personaldetail.DropDown;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalDetailSaveInfoServiceTest {
    @Mock
    private LendingServiceClient lendingServiceClient;

    PersonalDetailSaveInfoService personalDetailSaveInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        personalDetailSaveInfoService = new PersonalDetailSaveInfoService(lendingServiceClient);
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

        when(lendingServiceClient.saveCustomerInfo("001100000000000000000018593707",personalDetailSaveInfoRequest)).thenReturn(ResponseEntity.ok(mockPersonalDetailResponseData()));

        PersonalDetailResponse actualResult = personalDetailSaveInfoService.updatePersonalDetailInfo("001100000000000000000018593707",personalDetailSaveInfoRequest);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testSavePersonalDetailFailed() {

        PersonalDetailSaveInfoRequest request = new PersonalDetailSaveInfoRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.saveCustomerInfo("001100000000000000000018593707",request)).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                personalDetailSaveInfoService.updatePersonalDetailInfo(crmid,request));

    }

    private TmbOneServiceResponse<PersonalDetailResponse> mockPersonalDetailResponseData() {
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

}