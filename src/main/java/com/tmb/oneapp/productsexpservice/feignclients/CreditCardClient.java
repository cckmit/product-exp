package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardRequest;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardResponse;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.*;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadRequest;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetBilledStatementQuery;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetUnbilledStatementQuery;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import com.tmb.oneapp.productsexpservice.model.setpin.SetPinQuery;
import com.tmb.oneapp.productsexpservice.model.setpin.SetPinResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.creditcard.service.name}", url = "${feign.creditcard.service.url}")
public interface CreditCardClient {
    @GetMapping(value = "/apis/creditcard/creditcard-block-code/{ACCOUNT_ID}")
    ResponseEntity<GetCardBlockCodeResponse> getCardBlockCode(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @PathVariable(value = "ACCOUNT_ID") String accountId);

    @GetMapping(value = "/apis/creditcard/creditcard-details/{ACCOUNT_ID}")
    ResponseEntity<FetchCardResponse> getCreditCardDetails(@RequestHeader("X-Correlation-ID") String correlationId,
                                                           @PathVariable(value = "ACCOUNT_ID") String accountId);

    @PostMapping(value = "/apis/creditcard/credit-card/activate-card")
    ResponseEntity<ActivateCardResponse> activateCard(@RequestHeader Map<String, String> headers);

    @PostMapping(value = "/apis/creditcard/credit-card/activateCreditCard/verifyCvv")
    ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> verifyCvv(@RequestHeader Map<String, String> headers);

    @PostMapping(value = "/apis/creditcard/set-credit-limit")
    ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> fetchSetCreditLimit(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationID,
            @RequestBody SetCreditLimitReq requestBodyParameter);

    @GetMapping(value = "/apis/creditcard/credit-card/fetch-reason-list")
    ResponseEntity<TmbOneServiceResponse<List<Reason>>> getReasonList(
            @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId);

    @GetMapping(value = "/apis/creditcard/creditcard-billed-statement/{ACCOUNT_ID}")
    ResponseEntity<BilledStatementResponse> getBilledStatement(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @PathVariable(value = "ACCOUNT_ID") String accountId);

    @GetMapping(value = "/apis/creditcard/creditcard-unbilled-statement")
    ResponseEntity<BilledStatementResponse> getUnBilledStatement(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody GetUnbilledStatementQuery getUnBilledStatement);

    @PostMapping(value = "/apis/creditcard/creditcard-billed-statement-period/{ACCOUNT_ID}")
    ResponseEntity<BilledStatementResponse> getBilledStatementWithPeriod(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @PathVariable(value = "ACCOUNT_ID") String accountId,
            @RequestBody GetBilledStatementQuery billedStatementPeriodQuery);

    @PostMapping(value = "/apis/creditcard/block-card")
    ResponseEntity<BlockCardResponse> getBlockCardDetails(@RequestBody BlockCardRequest requestBodyParameter);

    @PostMapping(value = "/apis/creditcard/get-campaign-transactions")
    ResponseEntity<TmbOneServiceResponse<CampaignTransactionResponse>> getCampaignTransactionsDetails(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody CampaignTransactionQuery requestBodyParameter);

    @PostMapping(value = "/apis/creditcard/card-installment-confirm")
    ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse> >>  confirmCardInstallment(
            @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody CardInstallmentQuery requestBodyParameter);

    @GetMapping(value = "/apis/creditcard/fetch-installment-plan")
    ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> getInstallmentPlan(
            @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId);

    @PostMapping(value = "/apis/creditcard/set-pin")
    ResponseEntity<TmbOneServiceResponse<SetPinResponse>> setPin(
            @RequestHeader(value = ProductsExpServiceConstant.X_CORRELATION_ID) String correlationID,
            @RequestBody SetPinQuery requestBodyParameter);

    @PostMapping(value = "/installment/get-eligible-leads")
    ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getEligibleLeads(
            @RequestHeader(value = ProductsExpServiceConstant.X_CORRELATION_ID) String correlationID,
            @RequestBody EligibleLeadRequest requestBodyParameter);

    @PostMapping(value = "/installment/get-installment-rate")
    ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> getInstallmentRate(
            @RequestHeader(value = ProductsExpServiceConstant.X_CORRELATION_ID) String correlationID,
            @RequestBody InstallmentRateRequest requestBodyParameter);
}
