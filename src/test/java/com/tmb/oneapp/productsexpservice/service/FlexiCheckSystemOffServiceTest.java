package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.CommonData;
import com.tmb.common.model.FlexiLoanNoneServiceHour;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CheckSystemOffRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FlexiCheckSystemOffServiceTest {
    @Mock
    private CommonServiceClient commonServiceClient;

    FlexiCheckSystemOffService flexiCheckSystemOffService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        flexiCheckSystemOffService = new FlexiCheckSystemOffService(commonServiceClient);
    }

    @Test
    public void testCheckSystemOffSuccess() {
        TmbOneServiceResponse<List<CommonData>> oneServiceResponse = new TmbOneServiceResponse<List<CommonData>>();
        List<CommonData> commonDataList = new ArrayList<>();
        CommonData commonData = new CommonData();
        FlexiLoanNoneServiceHour hour = new FlexiLoanNoneServiceHour();
        hour.setEnd("09:00");
        hour.setStart("21:00");
        commonData.setFlexiLoanNoneServiceHour(hour);
        commonDataList.add(commonData);
        oneServiceResponse.setData(commonDataList);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        when(commonServiceClient.getCommonConfigByModule(any(),any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

        CheckSystemOffRequest request = new CheckSystemOffRequest();
        request.setCurrentTime("17:00");
        flexiCheckSystemOffService.checkSystemOff("001100000000000000000018593707", request);
        Assert.assertTrue(true);
    }

    @Test
    public void testCheckSystemOffFailed()   {
        TmbOneServiceResponse<List<CommonData>> oneServiceResponse = new TmbOneServiceResponse<List<CommonData>>();

        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(), "lending-service"));

        when(commonServiceClient.getCommonConfigByModule(any(),any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        CheckSystemOffRequest request = new CheckSystemOffRequest();
        request.setCurrentTime("17:00");
        assertThrows(Exception.class, () ->
                flexiCheckSystemOffService.checkSystemOff("001100000000000000000018593707", request));
    }



}