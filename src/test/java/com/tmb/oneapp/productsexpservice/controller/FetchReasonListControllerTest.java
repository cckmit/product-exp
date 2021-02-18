package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.Reason;

@RunWith(JUnit4.class)
public class FetchReasonListControllerTest {
	FetchReasonListController fetchReasonListController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		fetchReasonListController = new FetchReasonListController(creditCardClient);

	}

	@Test
	void testReasonListSuccess() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";
		TmbOneServiceResponse<List<Reason>> oneServiceResponse = new TmbOneServiceResponse<List<Reason>>();
		Reason reason = new Reason();
		reason.setReasonCode("200");
		List<Reason> list = new ArrayList<>();
		list.add(reason);
		ResponseEntity<TmbOneServiceResponse<List<Reason>>> response = new ResponseEntity<TmbOneServiceResponse<List<Reason>>>(
				oneServiceResponse, HttpStatus.OK);
		when(creditCardClient.getReasonList(anyString())).thenReturn(response);
		ResponseEntity<TmbOneServiceResponse<List<Reason>>> reasonRes = fetchReasonListController
				.reasonList(correlationId);
		assertEquals(200, reasonRes.getStatusCodeValue());

	}
	
	@Test
	void testReasonListSuccessNull() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";
		ResponseEntity<TmbOneServiceResponse<List<Reason>>> response = null;
		when(creditCardClient.getReasonList(anyString())).thenReturn(response);
		ResponseEntity<TmbOneServiceResponse<List<Reason>>> reasonRes = fetchReasonListController
				.reasonList(correlationId);
		assertEquals(400, reasonRes.getStatusCodeValue());

	}

	@Test
	void testReasonListError() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";
		when(creditCardClient.getReasonList(anyString())).thenThrow(RuntimeException.class);
		ResponseEntity<TmbOneServiceResponse<List<Reason>>> reasonRes = fetchReasonListController
				.reasonList(correlationId);
		assertNull(reasonRes.getBody().getData());
	}

}
