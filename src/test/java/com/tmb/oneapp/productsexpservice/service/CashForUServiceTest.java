package com.tmb.oneapp.productsexpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.CashForUConfigInfo;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.BalanceCredit;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardBalances;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardCashAdvance;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.loan.CashForYourResponse;
import com.tmb.oneapp.productsexpservice.model.loan.EnquiryInstallmentRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentData;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
import com.tmb.oneapp.productsexpservice.model.loan.ModelTenor;

@RunWith(JUnit4.class)
public class CashForUServiceTest {

	@Mock
	private CreditCardClient creditCardClient;

	private CashForUService cashForUservice;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		cashForUservice = new CashForUService(creditCardClient);
	}

	@Test
	public void testCaseChillChilltestCashAdvance() {
		InstallmentRateResponse installmentRateResponse = new InstallmentRateResponse();
		String cashChillChillFlag = "N";
		String cashTransferFlag = "N";
		String correlationId = "xxdasdvvd";
		EnquiryInstallmentRequest requestBody = new EnquiryInstallmentRequest();

		FetchCardResponse cardResponse = new FetchCardResponse();

		CreditCardDetail creditCardDetail = new CreditCardDetail();
		CardBalances cardBalance = new CardBalances();
		cardBalance.setAvailableCashAdvance(BigDecimal.TEN);
		cardBalance.setAvailableCreditAllowance(BigDecimal.TEN);
		creditCardDetail.setCardBalances(cardBalance);

		CardCashAdvance cashAdvance = new CardCashAdvance();
		cashAdvance.setCashAdvFeeFixedAmt(BigDecimal.ONE);
		cashAdvance.setCashAdvFeeRate(BigDecimal.ONE);
		cashAdvance.setCashAdvFeeVATRate(BigDecimal.ONE);
		cashAdvance.setCashAdvIntRate(BigDecimal.ONE);

		creditCardDetail.setCardCashAdvance(cashAdvance);

		cardResponse.setCreditCard(creditCardDetail);
		requestBody.setAmount("10000");
		
		TmbOneServiceResponse<CashForUConfigInfo> serviceResponse = new TmbOneServiceResponse<CashForUConfigInfo>();
		CashForUConfigInfo configInfo = new CashForUConfigInfo();
		
		List<String> productList = new ArrayList<String>();
		
		configInfo.setWaiveFeeProducts(productList);
		configInfo.setWaiveVatProducts(productList);
		serviceResponse.setData(configInfo);
		
		when(creditCardClient.getCreditCardDetails(any(), any())).thenReturn(ResponseEntity.ok().body(cardResponse));
		when(creditCardClient.getCurrentCashForYouRate()).thenReturn(ResponseEntity.ok().body(serviceResponse));
		InstallmentRateRequest rateRequest = new InstallmentRateRequest();
		CashForYourResponse cashResponse = cashForUservice.calculateInstallmentForCashForYou(rateRequest, correlationId,
				requestBody);
		Assert.assertNull(cashResponse.getInstallmentData());
		Assert.assertNotEquals("0", cashResponse.getCashFeeRate());
		Assert.assertNotEquals("0", cashResponse.getCashInterestRate());
		Assert.assertNotEquals("0", cashResponse.getCashVatRate());
	}

	@Test
	public void testCaseChillChill() {
		InstallmentRateResponse installmentRateResponse = new InstallmentRateResponse();
		InstallmentData cashChillChillInst = new InstallmentData();
		cashChillChillInst.setCashChillChillModel("Y");
		cashChillChillInst.setCashTransferModel("Y");
		List<ModelTenor> modelTenors = new ArrayList<ModelTenor>();
		cashChillChillInst.setModelTenors(modelTenors);
		installmentRateResponse.setInstallmentData(cashChillChillInst);

		String cashChillChillFlag = "Y";
		String cashTransferFlag = "Y";
		String correlationId = "xxdasdvvd";
		EnquiryInstallmentRequest requestBody = new EnquiryInstallmentRequest();

		FetchCardResponse cardResponse = new FetchCardResponse();

		CreditCardDetail creditCardDetail = new CreditCardDetail();
		CardBalances cardBalance = new CardBalances();
		cardBalance.setAvailableCashAdvance(BigDecimal.TEN);
		cardBalance.setAvailableCreditAllowance(BigDecimal.TEN);
		creditCardDetail.setCardBalances(cardBalance);

		CardCashAdvance cashAdvance = new CardCashAdvance();
		cashAdvance.setCashAdvFeeFixedAmt(BigDecimal.ZERO);
		cashAdvance.setCashAdvFeeRate(BigDecimal.ZERO);
		cashAdvance.setCashAdvFeeVATRate(BigDecimal.ZERO);
		cashAdvance.setCashAdvIntRate(BigDecimal.ZERO);
		creditCardDetail.setCardCashAdvance(cashAdvance);

		CardBalances cBalance = new CardBalances();
		BalanceCredit balanceCredit = new BalanceCredit();
		balanceCredit.setAvailableToTransfer(BigDecimal.ZERO);
		cBalance.setBalanceCreditLimit(balanceCredit);
		creditCardDetail.setCardBalances(cBalance);

		cardResponse.setCreditCard(creditCardDetail);

		TmbOneServiceResponse<CashForUConfigInfo> serviceResponse = new TmbOneServiceResponse<CashForUConfigInfo>();
		CashForUConfigInfo configInfo = new CashForUConfigInfo();
		
		List<String> productList = new ArrayList<String>();
		
		configInfo.setWaiveFeeProducts(productList);
		configInfo.setWaiveVatProducts(productList);
		serviceResponse.setData(configInfo);
		
		when(creditCardClient.getCreditCardDetails(any(), any())).thenReturn(ResponseEntity.ok().body(cardResponse));
		when(creditCardClient.getCurrentCashForYouRate()).thenReturn(ResponseEntity.ok(serviceResponse));
		InstallmentRateRequest rateRequest = new InstallmentRateRequest();
		rateRequest.setAmount("10000");
		requestBody.setAmount(rateRequest.getAmount());
		CashForYourResponse cashResponse = cashForUservice.calculateInstallmentForCashForYou(rateRequest, correlationId, requestBody);
		Assert.assertNull(cashResponse.getInstallmentData());
	}

}
