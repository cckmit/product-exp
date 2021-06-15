package com.tmb.oneapp.productsexpservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardBalances;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.loan.CashForYourResponse;
import com.tmb.oneapp.productsexpservice.model.loan.EnquiryInstallmentRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;

@Service
public class CashForUService {

	private CreditCardClient creditCardClient;

	public CashForUService(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	/**
	 * calculate cash for your model
	 * 
	 * @param installmentRateResponse
	 * @param cashTransferFlag
	 * @param cashChillChillFlag
	 * @param correlationId
	 * @param requestBody
	 * @return
	 */
	public CashForYourResponse calculateInstallmentForCashForYou(InstallmentRateResponse installmentRateResponse,
			String cashChillChillFlag, String cashTransferFlag, String correlationId,
			EnquiryInstallmentRequest requestBody) {
		CashForYourResponse responseModelInfo = new CashForYourResponse();
		if ("Y".equals(cashChillChillFlag) && "Y".equals(cashTransferFlag)) {
			responseModelInfo.setCashVatRate("0");
			responseModelInfo.setCashFeeRate("0");
			responseModelInfo.setCashInterestRate("0");
			responseModelInfo.setInstallmentData(installmentRateResponse.getInstallmentData());
			ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
					requestBody.getAccountId());
			CardBalances cardBalances = fetchCardResponse.getBody().getCreditCard().getCardBalances();
			responseModelInfo.setMaximumTransferAmt(String.valueOf(cardBalances.getBalanceCreditLimit().getAvailableToTransfer()));
		} else {
			calcualteForCaseCashAdvance(responseModelInfo, correlationId, requestBody);
		}
		return responseModelInfo;
	}

	/**
	 * 
	 * @param responseModelInfo
	 * @param correlationId
	 * @param requestBody
	 */
	private void calcualteForCaseCashAdvance(CashForYourResponse responseModelInfo, String correlationId,
			EnquiryInstallmentRequest requestBody) {
		ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
				requestBody.getAccountId());
		CreditCardDetail cardDetail = fetchCardResponse.getBody().getCreditCard();
		responseModelInfo.setCashFeeRate(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvFeeRate()));
		responseModelInfo.setCashInterestRate(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvIntRate()));
		responseModelInfo.setCashVatRate(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvFeeVATRate()));
		responseModelInfo.setMaximumTransferAmt(String.valueOf(cardDetail.getCardBalances().getAvailableCashAdvance()));
	}

}
