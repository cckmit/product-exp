package com.tmb.oneapp.productsexpservice.controller;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.CreditCardServiceHour;

@RunWith(JUnit4.class)
public class CreditCardServiceHourControllerTest {
	@Mock
	TMBLogger<CreditCardServiceHourController> logger;
	@InjectMocks
	CreditCardServiceHourController creditCardServiceHourController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		creditCardServiceHourController = new CreditCardServiceHourController(creditCardClient);
	}

	@Test
	public void testGetCreditCardServiceHour() {
		CreditCardServiceHour data = new CreditCardServiceHour();
		data.setApplyEStatementStarttimeEndtime("04:00-21:00");
		TmbOneServiceResponse<CreditCardServiceHour> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(creditCardClient.getCreditCardServiceHour()).thenReturn(ResponseEntity.ok(oneServiceResponse));
		Map<String, String> headers = new HashMap<String, String>();
		ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> result = creditCardServiceHourController
				.getCreditCardServiceHour(headers);
		Assert.assertNotEquals(400, result.getStatusCodeValue());
	}

}
