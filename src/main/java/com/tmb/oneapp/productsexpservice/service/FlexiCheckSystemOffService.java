package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.FlexiLoanNoneServiceHour;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CheckSystemOffResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class FlexiCheckSystemOffService {
    private static final TMBLogger<FlexiCheckSystemOffService> logger = new TMBLogger<>(FlexiCheckSystemOffService.class);
    private final CommonServiceClient commonServiceClient;

    public CheckSystemOffResponse checkSystemOff(String correlationId) {
        CheckSystemOffResponse response = new CheckSystemOffResponse();
        FlexiLoanNoneServiceHour systemHour = getAllConfig(correlationId).getData().get(0).getFlexiLoanNoneServiceHour();

        if (systemHour != null) {
            LocalTime current = LocalTime.parse((getCurrentTime()));
            LocalTime start = LocalTime.parse(systemHour.getStart());
            LocalTime end = LocalTime.parse(systemHour.getEnd());

            Boolean isNowInRange = (!current.isBefore(end)) && current.isBefore(start);
            response.setIsSystemOff(isNowInRange);
            response.setSystemOffTime(systemHour.getStart());
            response.setSystemOnTime(systemHour.getEnd());
        }

        return response;
    }

    private TmbOneServiceResponse<List<CommonData>> getAllConfig(String correlationId) {

        TmbOneServiceResponse<List<CommonData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> nodeTextResponse = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.LENDING_MODULE);
        if (nodeTextResponse.getBody().getData() != null) {
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
        }

        return oneTmbOneServiceResponse;
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String currentMinute = formatter.format(date);
        logger.info("currentMinute is  {} ", currentMinute);
        return currentMinute;

    }

}
