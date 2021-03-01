package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.creditcard.service.name}", url = "${feign.creditcard.service.url}")
public interface CreditCardClient {
	@GetMapping(value = "/apis/creditcard/creditcard-block-code/{ACCOUNT_ID}")
	public ResponseEntity<GetCardBlockCodeResponse> getCardBlockCode(
			@RequestHeader("X-Correlation-ID") String correlationId,
			@PathVariable(value = "ACCOUNT_ID") String accountId);

	@GetMapping(value = "/apis/creditcard/creditcard-details/{ACCOUNT_ID}")
	public ResponseEntity<GetCardResponse> getCreditCardDetails(@RequestHeader("X-Correlation-ID") String correlationId,
			@PathVariable(value = "ACCOUNT_ID") String accountId);

	@PostMapping(value = "/apis/creditcard/credit-card/activate-card")
	public ResponseEntity<ActivateCardResponse> activateCard(@RequestHeader Map<String, String> headers);

	@PostMapping(value = "/apis/creditcard/credit-card/activateCreditCard/verifyCvv")
	public ResponseEntity<VerifyCvvResponse> verifyCvv(@RequestHeader Map<String, String> headers);

	@PostMapping(value = "/apis/creditcard/set-credit-limit")
	public ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> fetchSetCreditLimit(
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationID,
			@RequestBody SetCreditLimitReq requestBodyParameter);

	@GetMapping(value = "/apis/creditcard/credit-card/fetch-reason-list")
	public ResponseEntity<TmbOneServiceResponse<List<Reason>>>  getReasonList(
			@RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId);

	@GetMapping(value = "/apis/creditcard/creditcard-billed-statement/{ACCOUNT_ID}")
	public ResponseEntity<BilledStatementResponse> getBilledStatement(
			 @RequestHeader("X-Correlation-ID") String correlationId,
			 @PathVariable(value = "ACCOUNT_ID") String accountId);

	@GetMapping(value = "/apis/creditcard/creditcard-unbilled-statement/{ACCOUNT_ID}")
	public ResponseEntity<BilledStatementResponse> getUnBilledStatement(
			@RequestHeader("X-Correlation-ID") String correlationId,
			@PathVariable(value = "ACCOUNT_ID") String accountId);

}
