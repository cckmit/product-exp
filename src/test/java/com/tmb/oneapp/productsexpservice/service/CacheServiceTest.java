package com.tmb.oneapp.productsexpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

	@Mock
	private TMBLogger<CacheServiceTest> logger;

	@Mock
	private CacheServiceClient cacheServiceClient;

	@InjectMocks
	private CacheService cacheService;

	@Test
	void removeCacheAfterSuccessCreditCardSuccess() throws IOException, TMBCommonException {

		TmbOneServiceResponse<String> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setData(new String());
		oneServiceResponse.setStatus(
				new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE,
						ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
		when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

		cacheService.removeCacheAfterSuccessCreditCard("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884");

		verify(cacheServiceClient, times(2)).deleteCacheByKey(anyString(), anyString());
	}

	@Test
	void removeCacheAfterSuccessCreditCardFail() throws IOException, TMBCommonException {

		TmbOneServiceResponse<String> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setData(null);
		oneServiceResponse.setStatus(
				new TmbStatus(ProductsExpServiceConstant.FAILED_ERROR_CODE, ProductsExpServiceConstant.FAIL_MESSAGE,
						ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.FAIL_MESSAGE));
		when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));

		cacheService.removeCacheAfterSuccessCreditCard("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884");

		verify(cacheServiceClient, times(0)).deleteCacheByKey(anyString(), anyString());
	}

}