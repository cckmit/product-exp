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
import com.tmb.common.model.Customer;
import com.tmb.common.model.Description;
import com.tmb.common.model.Profile;
import com.tmb.common.model.StatementFlag;
import com.tmb.common.model.Status;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.UpdateEStatmentResp;
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
		UpdateEStatmentResp data = new UpdateEStatmentResp();
		Status status = new Status();
		status.setCode("0000");
		Description desc = new Description();
		desc.setEn("desc");
		status.setDescription(desc);
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
		ResponseEntity<TmbOneServiceResponse<UpdateEStatmentResp>> result = applyEStatementController
				.getEStatement(headers);
		Assert.assertNotEquals(400, result.getStatusCodeValue());
	}

}
