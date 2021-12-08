package com.tmb.oneapp.productsexpservice.activitylog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogActivityService {

    @Value("${com.tmb.oneapp.service.activity.topic.name}")
    private String topicName;

    private final KafkaProducerService kafkaProducerService;

    private static final TMBLogger<LogActivityService> logger = new TMBLogger<>(LogActivityService.class);

    public LogActivityService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Method responsible for sending data to Kafka producer
     *
     * @param data
     */
    @Async
    @LogAround
    public void createLog(BaseEvent data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String output = mapper.writeValueAsString(data);
            logger.info("Activity Data request is  {} : ", output);
            kafkaProducerService.sendMessageAsync(topicName, output);
            logger.info("callPostEventService -  data posted to activity_service : {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.info("Unable to log the activity request : {}", e.toString());
        }
    }

    /**
     * Method to build common data for saving activity log
     *
     * @param crmId
     * @param ipAddress
     * @param response
     * @return BaseEvent
     */
    @LogAround
    public <T> BaseEvent buildCommonData(String crmId, String ipAddress, TmbOneServiceResponse<T> response) {
        BaseEvent baseEvent = new BaseEvent();
        baseEvent.setCrmId(UtilMap.fullCrmIdFormat(crmId));
        baseEvent.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        baseEvent.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        baseEvent.setIpAddress(ipAddress);

        if (ProductsExpServiceConstant.SUCCESS_CODE.equalsIgnoreCase(response.getStatus().getCode())) {
            baseEvent.setActivityStatus(ActivityLogStatus.SUCCESS.getStatus());
        } else {
            baseEvent.setActivityStatus(ActivityLogStatus.FAILURE.getStatus());
            baseEvent.setFailReason(response.getStatus().getDescription());
        }

        return baseEvent;
    }
}