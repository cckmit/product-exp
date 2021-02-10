package com.tmb.oneapp.productsexpservice.feignclients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardBlockCodeResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;

@FeignClient(name = "${feign.creditcard.service.name}", url = "${feign.creditcard.service.url}")
public interface ActivateCreditCardClient {
	@GetMapping(value = "/apis/creditcard/creditcard-block-code/{ACCOUNT_ID}")
	public ResponseEntity<GetCardBlockCodeResponse> getCardBlockCode(
			@RequestHeader("X-Correlation-ID") String correlationId,
			@PathVariable(value = "ACCOUNT_ID") String accountId);

	@GetMapping(value = "/apis/creditcard/creditcard-details/{ACCOUNT_ID}")
	public ResponseEntity<GetCardResponse> getCreditCardDetails(@RequestHeader("X-Correlation-ID") String correlationId,
			@PathVariable(value = "ACCOUNT_ID") String accountId);
}
