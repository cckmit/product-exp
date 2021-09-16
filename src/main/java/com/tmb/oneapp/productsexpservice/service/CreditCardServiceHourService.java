package com.tmb.oneapp.productsexpservice.service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.CreditCardServiceHour;

@Service
public class CreditCardServiceHourService {
	private static final TMBLogger<CreditCardServiceHourService> logger = new TMBLogger<>(
			CreditCardServiceHourService.class);
	private final CreditCardClient creditCardClient;

	public CreditCardServiceHourService(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	public ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> getCreditCardServiceHour() {
		ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> response = null;
		CreditCardServiceHour data = new CreditCardServiceHour();
		try {
			response = creditCardClient.getCreditCardServiceHour();
			if (response != null && response.getStatusCode() == HttpStatus.OK) {
				data = response.getBody().getData();
				data.setSetPinIsAvailable(checkAvailableTime(data.getSetPinStarttimeEndtime()));
				data.setApplyEStatementIsAvailable(checkAvailableTime(data.getApplyEStatementStarttimeEndtime()));
				data.setApplyEStatementLoanIsAvailable(
						checkAvailableTime(data.getApplyEStatementLoanStarttimeEndtime()));
				data.setApplySogooodIsAvailable(checkAvailableTime(data.getApplySogooodStarttimeEndtime()));
				data.setCardDetailsIsAvailable(checkAvailableTime(data.getCardDetailsStarttimeEndtime()));
				data.setChangeCreditLimitIsAvailable(checkAvailableTime(data.getChangeCreditLimitStarttimeEndtime()));
				response.getBody().setData(data);
			}
		} catch (Exception e) {
			logger.info("Unable to get service hour config : {}", e);
			throw e;
		}
		return response;
	}

	private String checkAvailableTime(String starttimeEndtime) {
		String result = "N";
		String startTime = starttimeEndtime.substring(0, 5);
		String endTime = starttimeEndtime.substring(6, 11);
		LocalTime curDate = LocalTime.parse(getCurrentTimeStamp());
		if (curDate.isAfter(LocalTime.parse(startTime)) && curDate.isBefore(LocalTime.parse(endTime))) {
			result = "Y";
		}
		return result;
	}
	
	private String getCurrentTimeStamp() {
	    return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
	}
}
