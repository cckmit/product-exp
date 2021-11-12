package com.tmb.oneapp.productsexpservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.model.CashForUConfigInfo;
import com.tmb.common.model.TmbOneServiceResponse;
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

	private static String FIXED_RATE = "Fixed Rate";
	private static String EFFECTED_RATE = "Effective Rate";

	public CashForUService(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
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
		ResponseEntity<TmbOneServiceResponse<CashForUConfigInfo>> response = creditCardClient
				.getCurrentCashForYouRate();
		CashForUConfigInfo rateCashForUInfo = response.getBody().getData();
		if ("Y".equals(requestBody.getCashChillChillFlag()) && "Y".equals(requestBody.getCashTransferFlag())) {

			ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> loanResponse = creditCardClient
					.getInstallmentRate(correlationId, rateRequest);
			InstallmentRateResponse installmentRateResponse = loanResponse.getBody().getData();

			responseModelInfo.setInstallmentData(installmentRateResponse.getInstallmentData());
			ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
					requestBody.getAccountId());

			processResponRateType(responseModelInfo, rateCashForUInfo, requestBody,
					fetchCardResponse.getBody().getCreditCard().getProductId());

			CardBalances cardBalances = fetchCardResponse.getBody().getCreditCard().getCardBalances();
			String leadRate = fillterForRateCashTrasfer(installmentRateResponse);
			responseModelInfo.setCashInterestRate(formateDigit(leadRate));
			responseModelInfo.setMaximumTransferAmt(
					formateDigit(String.valueOf(cardBalances.getBalanceCreditLimit().getAvailableToTransfer())));

			BigDecimal cashTransferFee = new BigDecimal(rateCashForUInfo.getCashTransferFee());
			BigDecimal cashTransferVat = new BigDecimal(rateCashForUInfo.getCashTransferVat());
			BigDecimal cashChillFee = new BigDecimal(rateCashForUInfo.getCashChillFee());
			BigDecimal cashChillVat = new BigDecimal(rateCashForUInfo.getCashChillVat());
			BigDecimal feeTransfer = BigDecimal.ZERO;
			BigDecimal feeChill = BigDecimal.ZERO;
			if (allowWaiveFeeProduct(rateCashForUInfo, fetchCardResponse.getBody().getCreditCard().getProductId())) {
				responseModelInfo.setCashFeeRate(formateDigit(BigDecimal.ZERO.toString()));
				responseModelInfo.setFeeCashTransfer(formateDigit(BigDecimal.ZERO.toString()));
				responseModelInfo.setFeeCashChillChill(formateDigit(BigDecimal.ZERO.toString()));
			} else {
				feeTransfer = new BigDecimal(
						requestBody.getAmount() != null ? requestBody.getAmount() : BigDecimal.ZERO.toString())
								.multiply(cashTransferFee);
				feeChill = new BigDecimal(
						requestBody.getAmount() != null ? requestBody.getAmount() : BigDecimal.ZERO.toString())
								.multiply(cashChillFee);
				responseModelInfo.setCashFeeRate(formateDigit(feeTransfer.toString()));
				responseModelInfo.setFeeCashTransfer(formateDigit(feeTransfer.toString()));
				responseModelInfo.setFeeCashChillChill(formateDigit(feeChill.toString()));
			}
			if (allowWaiveVatProduct(rateCashForUInfo, fetchCardResponse.getBody().getCreditCard().getProductId())) {
				responseModelInfo.setCashVatRate(formateDigit(BigDecimal.ZERO.toString()));
				responseModelInfo.setVatCashTransfer(formateDigit(BigDecimal.ZERO.toString()));
				responseModelInfo.setVatCashChillChill(formateDigit(BigDecimal.ZERO.toString()));
			} else {
				feeTransfer = feeTransfer.multiply(cashTransferVat);
				feeChill = feeChill.multiply(cashChillVat);
				responseModelInfo.setCashVatRate(formateDigit(feeTransfer.toString()));
				responseModelInfo.setVatCashTransfer(formateDigit(feeTransfer.toString()));
				responseModelInfo.setVatCashChillChill(formateDigit(feeChill.toString()));
			}

		} else {
			calcualteForCaseCashAdvance(responseModelInfo, correlationId, requestBody, rateCashForUInfo);
		}
		return responseModelInfo;
	}

	/**
	 * Process response by type
	 * 
	 * @param responseModelInfo
	 * @param rateCashForUInfo
	 * @param requestBody
	 * @param productId
	 */
	private void processResponRateType(CashForYourResponse responseModelInfo, CashForUConfigInfo rateCashForUInfo,
			EnquiryInstallmentRequest requestBody, String productId) {
		List<String> targetProducts = rateCashForUInfo.getEffRateProducts();
		boolean isMatch = false;
		if (CollectionUtils.isNotEmpty(targetProducts)) {
			for (String code : targetProducts) {
				if (productId.equals(code) && (Integer.parseInt(requestBody.getBillCycleCutDate()) <= Integer
						.parseInt(rateCashForUInfo.getNoneFlashMonth()))) {
					isMatch = true;
				}
			}
		}

		if (isMatch) {
			responseModelInfo.setRateCaculationInfo(FIXED_RATE);
		} else {
			responseModelInfo.setRateCaculationInfo(EFFECTED_RATE);
		}

	}

	/**
	 * Format with 2 digit
	 * 
	 * @param cashTransferVat
	 * @return
	 */
	private String formateDigit(String cashTransferVat) {

		BigDecimal number = new BigDecimal(cashTransferVat);
		number = number.setScale(2, RoundingMode.HALF_UP);

		return number.toString();
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
		} else {
			rateCashTrasfer = "0.00";
		}
		return rateCashTrasfer;
	}

	/**
	 * 
	 * @param responseModelInfo
	 * @param correlationId
	 * @param requestBody
	 * @param rateCashForUInfo
	 */
	private void calcualteForCaseCashAdvance(CashForYourResponse responseModelInfo, String correlationId,
			EnquiryInstallmentRequest requestBody, CashForUConfigInfo rateCashForUInfo) {
		ResponseEntity<FetchCardResponse> fetchCardResponse = creditCardClient.getCreditCardDetails(correlationId,
				requestBody.getAccountId());

		CreditCardDetail cardDetail = fetchCardResponse.getBody().getCreditCard();

		processResponRateType(responseModelInfo, rateCashForUInfo, requestBody, cardDetail.getProductId());

		responseModelInfo.setMaximumTransferAmt(String.valueOf(cardDetail.getCardBalances().getAvailableCashAdvance()));

		BigDecimal feeAmt = cardDetail.getCardCashAdvance().getCashAdvFeeRate().divide(new BigDecimal("100"))
				.multiply(new BigDecimal(requestBody.getAmount()));
		BigDecimal summaryFeee = cardDetail.getCardCashAdvance().getCashAdvFeeFixedAmt();
		if ("1".equals(cardDetail.getCardCashAdvance().getCashAdvFeeModel())) {
			if (feeAmt.compareTo(cardDetail.getCardCashAdvance().getCashAdvFeeFixedAmt()) > 0) {
				summaryFeee = feeAmt;
			}
			responseModelInfo.setCashFeeRate(formateDigit(String.valueOf(summaryFeee)));
		} else if ("2".equals(cardDetail.getCardCashAdvance().getCashAdvFeeModel())) {
			BigDecimal totalFee = feeAmt.add(summaryFeee);
			responseModelInfo.setCashFeeRate(formateDigit(String.valueOf(totalFee)));
		} else {
			responseModelInfo.setCashFeeRate(formateDigit(String.valueOf(feeAmt)));
		}

		responseModelInfo
				.setCashInterestRate(formateDigit(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvIntRate())));
		responseModelInfo
				.setCashVatRate(formateDigit(String.valueOf(cardDetail.getCardCashAdvance().getCashAdvFeeVATRate())));
		responseModelInfo.setCashVatTotal(calculationVatTotalNoneLead(responseModelInfo));

	}

	/**
	 * Validate product for waive fee zero
	 * 
	 * @param rateCashForUInfo
	 * @param productId
	 * @return
	 */
	private boolean allowWaiveVatProduct(CashForUConfigInfo rateCashForUInfo, String productId) {
		List<String> waiveVatProducts = rateCashForUInfo.getWaiveVatProducts();
		boolean isAllow = false;
		if (CollectionUtils.isNotEmpty(waiveVatProducts)) {
			for (String code : waiveVatProducts) {
				if (productId.equals(code)) {
					isAllow = true;
				}
			}
		}
		return isAllow;
	}

	/**
	 * Validate product for waive vat zero
	 * 
	 * @param rateCashForUInfo
	 * @param productId
	 * @return
	 */
	private boolean allowWaiveFeeProduct(CashForUConfigInfo rateCashForUInfo, String productId) {
		List<String> waiveFeeProducts = rateCashForUInfo.getWaiveFeeProducts();
		boolean isAllow = false;
		if (CollectionUtils.isNotEmpty(waiveFeeProducts)) {
			for (String code : waiveFeeProducts) {
				if (productId.equals(code)) {
					isAllow = true;
				}
			}
		}
		return isAllow;
	}

	/**
	 * Calculation vat total None lead
	 * 
	 * @param responseModelInfo
	 * @return
	 */
	private String calculationVatTotalNoneLead(CashForYourResponse responseModelInfo) {
		BigDecimal cashFeeRate = new BigDecimal(responseModelInfo.getCashFeeRate());
		BigDecimal cashVatRate = new BigDecimal(responseModelInfo.getCashVatRate());
		BigDecimal totalVatAmt = cashVatRate.multiply(cashFeeRate);
		return formateDigit(totalVatAmt.toString());
	}

}
