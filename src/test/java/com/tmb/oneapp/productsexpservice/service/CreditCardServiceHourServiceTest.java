package com.tmb.oneapp.productsexpservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class CreditCardServiceHourServiceTest {
	@Mock
	TMBLogger<CreditCardServiceHourService> logger;
	@Mock
	CreditCardClient creditCardClient;

	@InjectMocks
	CreditCardServiceHourService creditCardServiceHourService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		creditCardServiceHourService = new CreditCardServiceHourService(creditCardClient);
	}

	@Test
	public void testGetCreditCardServiceHourSuccess() throws Exception {
		CreditCardServiceHour data = new CreditCardServiceHour();
		data.setSetPinStarttimeEndtime("04:00-21:00");
		data.setApplyEStatementStarttimeEndtime("04:00-21:00");
		data.setApplyEStatementLoanStarttimeEndtime("04:00-21:00");
		data.setApplySogooodStarttimeEndtime("04:00-21:00");
		data.setCardDetailsStarttimeEndtime("04:00-21:00");
		data.setChangeCreditLimitStarttimeEndtime("04:00-21:00");
		TmbOneServiceResponse<CreditCardServiceHour> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(creditCardClient.getCreditCardServiceHour()).thenReturn(ResponseEntity.ok(oneServiceResponse));

		ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> result = creditCardServiceHourService
				.getCreditCardServiceHour();
		Assert.assertNotEquals(400, result.getStatusCodeValue());
	}
	
	@Test
	public void testGetCreditCardServiceHourError() throws Exception {
		CreditCardServiceHour data = new CreditCardServiceHour();
		data.setSetPinStarttimeEndtime("04:00-21:00");
		data.setApplyEStatementStarttimeEndtime("04:00-21:00");
		data.setApplyEStatementLoanStarttimeEndtime("04:00-21:00");
		data.setApplySogooodStarttimeEndtime("04:00-21:00");
		data.setCardDetailsStarttimeEndtime("04:00-21:00");
		data.setChangeCreditLimitStarttimeEndtime("04:0021:00");
		TmbOneServiceResponse<CreditCardServiceHour> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(creditCardClient.getCreditCardServiceHour()).thenReturn(ResponseEntity.ok(oneServiceResponse));
		assertThrows(Exception.class, () -> creditCardServiceHourService.getCreditCardServiceHour());
	}

}
