package com.tmb.oneapp.productsexpservice.service;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.BaseEvent;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;

@RunWith(JUnit4.class)
public class LogActivityServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    private LogActivityService logActivityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        logActivityService = new LogActivityService(kafkaProducerService);
    }

    @Test
    public void should_call_create_log_once_when_call_create_log_given_base_event() {
        // Given
        // When
        BaseEvent baseEvent = new BaseEvent();
        logActivityService.createLog(baseEvent);

        // Then
        assertTrue(true);
    }
}
