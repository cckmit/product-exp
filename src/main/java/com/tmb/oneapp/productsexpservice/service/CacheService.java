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
		String creditcardAccountsSummaryReqKey = String.format("%s_creditcards_accounts_summary_req", fullCrmId);
		String creditcardAccountsSummaryResKey = String.format("%s_creditcards_accounts_summary_res", fullCrmId);
		try {
			ResponseEntity<TmbOneServiceResponse<String>> responseCache = cacheServiceClient
					.getCacheByKey(correlationId, creditcardWithCrmIdKey);
			if (responseCache.getBody().getData() != null) {
				cacheServiceClient.deleteCacheByKey(correlationId, creditcardWithCrmIdKey);
				logger.info("========== remove cache creditcard success ==========");
			}

			ResponseEntity<TmbOneServiceResponse<String>> responseCacheAccountsSummaryReqKey = cacheServiceClient
					.getCacheByKey(correlationId, creditcardAccountsSummaryReqKey);
			if (responseCacheAccountsSummaryReqKey.getBody().getData() != null) {
				cacheServiceClient.deleteCacheByKey(correlationId, creditcardAccountsSummaryReqKey);
				logger.info("========== remove cache creditcardAccountsSummaryReqKey success ==========");
			}

			ResponseEntity<TmbOneServiceResponse<String>> responseCacheAccountsSummaryResKey = cacheServiceClient
					.getCacheByKey(correlationId, creditcardAccountsSummaryResKey);
			if (responseCacheAccountsSummaryResKey.getBody().getData() != null) {
				cacheServiceClient.deleteCacheByKey(correlationId, creditcardAccountsSummaryResKey);
				logger.info("========== remove cache creditcardAccountsSummaryResKey success ==========");
			}

		} catch (Exception ex) {
			logger.info("========== Can't Remove Key Redis complete ==========");
			logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
		}
	}
}
