package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.MonthlyTrans;
import com.tmb.oneapp.productsexpservice.model.loan.CashForYourResponse;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class InstallmentService {

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	public static final Integer CALCULATE_DIGIT = 5;
	public static final Integer DISPLAY_DIGIT = 2;

	private InstallmentService() {
	}

	/**
	 * @param principle
	 * @param tenor
	 * @param interestRatePercent
	 * @return
	 */
	public static MonthlyTrans calcualteMonthlyTransection(BigDecimal principle, Integer tenor,
			BigDecimal interestRatePercent) {
		MonthlyTrans monthlyTrans = new MonthlyTrans();
		BigDecimal totalInterest = principle.multiply(new BigDecimal(tenor)).multiply(interestRatePercent)
				.divide(ONE_HUNDRED, CALCULATE_DIGIT, RoundingMode.HALF_UP);
		BigDecimal monthlyPrinciple = principle.divide(new BigDecimal(tenor), CALCULATE_DIGIT, RoundingMode.HALF_UP);
		BigDecimal monthlyPrinciple2Digit = principle.divide(new BigDecimal(tenor), DISPLAY_DIGIT, RoundingMode.DOWN);
		BigDecimal monthlyInterest = totalInterest.divide(new BigDecimal(tenor), CALCULATE_DIGIT, RoundingMode.HALF_UP);
		BigDecimal monthlyInterest2Digit = totalInterest.divide(new BigDecimal(tenor), DISPLAY_DIGIT,
				RoundingMode.DOWN);

		BigDecimal firstPayment = monthlyPrinciple2Digit
				.add(monthlyPrinciple.subtract(monthlyPrinciple2Digit).multiply(new BigDecimal(tenor)))
				.add(monthlyInterest2Digit).setScale(DISPLAY_DIGIT, RoundingMode.HALF_UP);
		BigDecimal totalAmt = monthlyPrinciple2Digit.add(monthlyInterest2Digit)
				.setScale(DISPLAY_DIGIT, RoundingMode.HALF_UP).multiply(new BigDecimal(tenor).subtract(BigDecimal.ONE))
				.add(firstPayment);

		monthlyTrans.setFirstPayment(firstPayment);
		monthlyTrans.setInterest(monthlyInterest);
		monthlyTrans.setInterest2Digit(monthlyInterest2Digit);
		monthlyTrans.setPrinciple(monthlyPrinciple);
		monthlyTrans.setPrinciple2Digit(monthlyPrinciple2Digit);
		monthlyTrans.setTotalAmt(totalAmt);
		monthlyTrans.setTotalInterest(totalInterest);

		return monthlyTrans;
	}

	

}
