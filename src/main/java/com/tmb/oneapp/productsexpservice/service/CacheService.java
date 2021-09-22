package com.tmb.oneapp.productsexpservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;

@Service
public class CacheService {

	private static final TMBLogger<CacheService> logger = new TMBLogger<>(CacheService.class);

	private CacheServiceClient cacheServiceClient;

	@Autowired
	public CacheService(CacheServiceClient cacheServiceClient) {
		this.cacheServiceClient = cacheServiceClient;
	}

	public void removeCacheAfterSuccessCreditCard(String correlationId, String fullCrmId) {
		String creditcardWithCrmIdKey = String.format("%s_creditcard", fullCrmId);
		try {
			ResponseEntity<TmbOneServiceResponse<String>> responseCache = cacheServiceClient
					.getCacheByKey(correlationId, creditcardWithCrmIdKey);
			if (ProductsExpServiceConstant.SUCCESS_CODE.equals(responseCache.getBody().getStatus().getCode())) {
				if (responseCache.getBody().getData() != null) {
					cacheServiceClient.deleteCacheByKey(correlationId, creditcardWithCrmIdKey);
					logger.info("========== remove cache success ==========");
				}
			}

		} catch (Exception ex) {
			logger.info("========== Can't Remove Key Redis complete ==========");
			logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
		}
	}
}
