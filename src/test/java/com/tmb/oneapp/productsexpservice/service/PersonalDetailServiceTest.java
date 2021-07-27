package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.personaldetail.Address;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalDetailServiceTest {

    @Mock
    private LendingServiceClient lendingServiceClient;

    PersonalDetailService personalDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        personalDetailService = new PersonalDetailService(lendingServiceClient);
    }

    @Test
    public void testGetPersonalDetailSuccess() throws TMBCommonException {

        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        when(lendingServiceClient.getPersonalDetail(crmid,request.getCaId())).thenReturn(ResponseEntity.ok(mockPersonalDetailResponseData()));

        PersonalDetailResponse actualResult = personalDetailService.getPersonalDetailInfo(crmid,request);

        Assert.assertNotNull(actualResult);

    }

    @Test
    public void testGetPersonalDetailFailed() {

        PersonalDetailRequest request = new PersonalDetailRequest();
        request.setCaId(2021071404188196L);
        String crmid = "001100000000000000000018593707";

        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));

        when(lendingServiceClient.getPersonalDetail(anyString(),anyLong())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        assertThrows(Exception.class, () ->
                personalDetailService.getPersonalDetailInfo(crmid,request));

    }

    private TmbOneServiceResponse<PersonalDetailResponse> mockPersonalDetailResponseData() {
        TmbOneServiceResponse<PersonalDetailResponse> oneServiceResponse = new TmbOneServiceResponse<PersonalDetailResponse>();

        PersonalDetailResponse response = new PersonalDetailResponse();
        Address address = new Address();
        List<Resident> residentList = new ArrayList<>();
        Resident resident = new Resident();
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


        response.setBirthDate("11/10/33");
        response.setEmail("kk@gmail.com");
        response.setEngName("Test");
        response.setEngSurName("Ja");
        response.setExpiryDate("11/11/63");
        response.setIdIssueCtry1("dd");
        response.setMobileNo("0987654321");
        response.setNationality("TH");
        response.setThaiName("ทีทีบี");
        response.setThaiSurName("แบงค์");
        response.setThaiSalutationCode("1800272993728");
        response.setAddress(address);
        response.setResidentFlag(residentList);

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(response);

        return oneServiceResponse;

    }




}