package com.tmb.oneapp.productsexpservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
import com.tmb.oneapp.productsexpservice.model.loan.ModelTenor;
import com.tmb.oneapp.productsexpservice.model.loan.PricingTier;

@Service
public class CashForUService {

	private CreditCardClient creditCardClient;

	private CommonServiceClient commonServiceClient;

	private CashForUConfigInfo rateCashForUInfo = null;

	public CashForUService(CreditCardClient creditCardClient, CommonServiceClient commonServiceClient) {
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
	}

	/**
	 * calculate cash for your model
	 * 
	 * @param rateRequest
	 * @param correlationId
	 * @param requestBody
	 * @return
	 */
	public CashForYourResponse calculateInstallmentForCashForYou(InstallmentRateRequest rateRequest,
			String correlationId, EnquiryInstallmentRequest requestBody) {
		CashForYourResponse responseModelInfo = new CashForYourResponse();
		if ("Y".equals(requestBody.getCashChillChillFlag()) && "Y".equals(requestBody.getCashTransferFlag())) {

			ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> loanResponse = creditCardClient
					.getInstallmentRate(correlationId, rateRequest);
			InstallmentRateResponse installmentRateResponse = loanResponse.getBody().getData();
			if (Objects.isNull(rateCashForUInfo)) {
				ResponseEntity<TmbOneServiceResponse<CashForUConfigInfo>> response = commonServiceClient
						.getCurrentCashForYouRate();
				rateCashForUInfo = response.getBody().getData();
			}
			responseModelInfo.setCashVatRate(formateDigit(rateCashForUInfo.getCashTransferVat()));
			responseModelInfo.setCashFeeRate(formateDigit(rateCashForUInfo.getCashTransferFee()));
			responseModelInfo.setInstallmentData(installmentRateResponse.getInstallmentData());
			ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
					requestBody.getAccountId());
			CardBalances cardBalances = fetchCardResponse.getBody().getCreditCard().getCardBalances();
			String leadRate = fillterForRateCashTrasfer(installmentRateResponse);
			responseModelInfo.setCashInterestRate(formateDigit(leadRate));
			responseModelInfo.setMaximumTransferAmt(
					formateDigit(String.valueOf(cardBalances.getBalanceCreditLimit().getAvailableToTransfer())));
		} else {
			calcualteForCaseCashAdvance(responseModelInfo, correlationId, requestBody);
		}
		return responseModelInfo;
	}

	/**
	 * Format with 2 digit
	 * 
	 * @param cashTransferVat
	 * @return
	 */
	private String formateDigit(String cashTransferVat) {
		String returnFormate = cashTransferVat;
		try {
			BigDecimal number = new BigDecimal(cashTransferVat);
			number.setScale(2);
			returnFormate = number.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return returnFormate;
	}

	/**
	 * Find rate for cash transfer amount
	 * 
	 * @param installmentRateResponse
	 * @return
	 */
	private String fillterForRateCashTrasfer(InstallmentRateResponse installmentRateResponse) {
		List<ModelTenor> modelTenors = installmentRateResponse.getInstallmentData().getModelTenors();
		String rateCashTrasfer = null;
		ModelTenor cashTransferModel = null;
		for (ModelTenor tenors : modelTenors) {
			if ("CT".equals(tenors.getModelType())) {
				cashTransferModel = tenors;
			}
		}
		if (Objects.nonNull(cashTransferModel)) {
			List<PricingTier> pricingTier = cashTransferModel.getPricingTiers();
			rateCashTrasfer = pricingTier.get(0).getRate();
		}

		return rateCashTrasfer;
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
		responseModelInfo.setMaximumTransferAmt(String.valueOf(cardDetail.getCardBalances().getAvailableCashAdvance()));
		BigDecimal feeAmt = cardDetail.getCardCashAdvance().getCashAdvFeeRate().divide(new BigDecimal("100"))
				.multiply(new BigDecimal(requestBody.getAmount()));
		BigDecimal summaryFeee = cardDetail.getCardCashAdvance().getCashAdvFeeFixedAmt();
		if ("1".equals(cardDetail.getCardCashAdvance().getCashAdvFeeModel())) {
			if (feeAmt.compareTo(cardDetail.getCardCashAdvance().getCashAdvFeeFixedAmt()) > 0) {
				summaryFeee = feeAmt;
			}
			responseModelInfo.setCashFeeRate(String.valueOf(summaryFeee));
		} else if ("2".equals(cardDetail.getCardCashAdvance().getCashAdvFeeModel())) {
			BigDecimal totalFee = feeAmt.add(summaryFeee);
			responseModelInfo.setCashFeeRate(String.valueOf(totalFee));
		} else {
			responseModelInfo.setCashFeeRate(String.valueOf(feeAmt));
		}
		responseModelInfo.setCashInterestRate(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvIntRate()));
		responseModelInfo.setCashVatRate(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvFeeVATRate()));

	}

	public void setRateCashForUInfo(CashForUConfigInfo rateCashForUInfo) {
		this.rateCashForUInfo = rateCashForUInfo;
	}

}
