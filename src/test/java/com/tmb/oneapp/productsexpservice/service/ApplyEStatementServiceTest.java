package com.tmb.oneapp.productsexpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;

@RunWith(JUnit4.class)
public class ApplyEStatementServiceTest {

	@Mock
	private CustomerServiceClient customerServiceClient;

	ApplyEStatementService applyEStatementService;
	CreditCardClient creditCardClient;
	AccountRequestClient accountReqClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		applyEStatementService = new ApplyEStatementService(customerServiceClient,creditCardClient,accountReqClient);
	}

	@Test
	void testGetEStatement() throws Exception {
		String correlationId = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
		String crmId = ProductsExpServiceConstant.X_CRMID;
		TmbOneServiceResponse<ApplyEStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
		ApplyEStatementResponse data = new ApplyEStatementResponse();
		oneServiceResponse.setData(data);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerEStatement(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
		applyEStatementService.getEStatement(crmId, correlationId);
		Assert.assertNotNull(ResponseEntity.ok(oneServiceResponse));
	}

}
