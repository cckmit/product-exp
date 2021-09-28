package com.tmb.oneapp.productsexpservice.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.model.applyestatement.Customer;
import com.tmb.oneapp.productsexpservice.model.applyestatement.Profile;
import com.tmb.oneapp.productsexpservice.model.applyestatement.StatementFlag;
import com.tmb.oneapp.productsexpservice.model.applyestatement.Status;
import com.tmb.oneapp.productsexpservice.service.ApplyEStatementService;
import com.tmb.oneapp.productsexpservice.service.CacheService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;

@RunWith(JUnit4.class)
public class ApplyEStatementControllerTest {
	@Mock
	TMBLogger<ApplyEStatementController> logger;
	@Mock
	ApplyEStatementService applyEStatementService;
	@InjectMocks
	ApplyEStatementController applyEStatementController;
	@Mock
	NotificationService notificationService;
	@Mock
	CacheService cacheService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		applyEStatementController = new ApplyEStatementController(applyEStatementService, notificationService,
				cacheService);
	}

	@Test
	public void testGetApplyEStatement() {
		ApplyEStatementResponse data = new ApplyEStatementResponse();
		Status status = new Status();
		status.setCode("0000");
		status.setDescription("desc");
		Customer customer = new Customer();
		Profile profile = new Profile();
		profile.setCcId("ccid");
		StatementFlag statementFlag = new StatementFlag();
		customer.setProfile(profile);
		customer.setStatementFlag(statementFlag);
		data.setStatus(status);
		data.setCustomer(customer);
		when(applyEStatementService.getEStatement(any(), any())).thenReturn(data);

		Map<String, String> headers = new HashMap<String, String>();
		ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> result = applyEStatementController
				.getEStatement(headers);
		Assert.assertNotEquals(400, result.getStatusCodeValue());
	}

}
