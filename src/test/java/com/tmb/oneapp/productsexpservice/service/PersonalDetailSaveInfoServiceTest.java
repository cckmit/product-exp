package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.Header;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.ResponseIndividual;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.Resident;
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
        Resident resident = new Resident();
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

        when(lendingServiceClient.saveCustomerInfo(personalDetailSaveInfoRequest)).thenReturn(ResponseEntity.ok(mockPersonalDetailResponseData()));

        ResponseIndividual actualResult = personalDetailSaveInfoService.updatePersonalDetailInfo(personalDetailSaveInfoRequest);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetPersonalDetailFailed() {

        PersonalDetailSaveInfoRequest request = new PersonalDetailSaveInfoRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<ResponseIndividual> oneServiceResponse = new TmbOneServiceResponse<ResponseIndividual>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.saveCustomerInfo(request)).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                personalDetailSaveInfoService.updatePersonalDetailInfo(request));

    }

    private TmbOneServiceResponse<ResponseIndividual> mockPersonalDetailResponseData() {
        TmbOneServiceResponse<ResponseIndividual> oneServiceResponse = new TmbOneServiceResponse<ResponseIndividual>();

        ResponseIndividual response = new ResponseIndividual();
        Header header = new Header();
        header.setResponseCode("MSG_000");
        response.setHeader(header);
        response.setBody(null);

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(response);

        return oneServiceResponse;

    }

}