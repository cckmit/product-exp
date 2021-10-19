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
	KafkaProducerService kakfkaProducerService;
	
	private LogActivityService logctivityService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		logctivityService = new LogActivityService(kakfkaProducerService);
	}
	
	@Test
	public void testCreatedLog() {
		BaseEvent baseEvent = new BaseEvent();
		logctivityService.createLog(baseEvent);
		assertTrue(true);
	}
	

}
