package com.tmb.oneapp.productsexpservice.service;

import javax.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.model.CashForUConfigInfo;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
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

	private CommonServiceClient commonServiceClient;

	private static CashForUConfigInfo rateCashForUInfo = null;

	@PostConstruct
	public void doCachRate() {
		ResponseEntity<TmbOneServiceResponse<CashForUConfigInfo>> response = commonServiceClient
				.getCurrentCashForYouRate();
		rateCashForUInfo = response.getBody().getData();
	}

	public CashForUService(CreditCardClient creditCardClient, CommonServiceClient commonServiceClient) {
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
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
			responseModelInfo.setCashVatRate(rateCashForUInfo.getCashTransferVat());
			responseModelInfo.setCashFeeRate(rateCashForUInfo.getCashTransferFee());
			responseModelInfo.setInstallmentData(installmentRateResponse.getInstallmentData());
			ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
					requestBody.getAccountId());
			CardBalances cardBalances = fetchCardResponse.getBody().getCreditCard().getCardBalances();
			responseModelInfo.setCashInterestRate(String
					.valueOf(fetchCardResponse.getBody().getCreditCard().getCardCashAdvance().getCashAdvIntRate()));
			responseModelInfo.setMaximumTransferAmt(
					String.valueOf(cardBalances.getBalanceCreditLimit().getAvailableToTransfer()));
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


	public static void setRateCashForUInfo(CashForUConfigInfo rateCashForUInfo) {
		CashForUService.rateCashForUInfo = rateCashForUInfo;
	}
	
}
