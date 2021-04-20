package com.tmb.oneapp.productsexpservice.service;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tmb.oneapp.productsexpservice.model.MonthlyTrans;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class InstallmentServiceTest {


	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		
	}

	@Test
	void testCalcualtationCase1() {
		MonthlyTrans result = new MonthlyTrans();
		result.setFirstPayment(new BigDecimal("173.66"));
		result.setInterest(new BigDecimal("6.90352"));
		result.setInterest2Digit(new BigDecimal("6.90000"));
		result.setPrinciple(new BigDecimal("166.75167"));
		result.setPrinciple2Digit(new BigDecimal("166.75000"));
		result.setTotalAmt(new BigDecimal("1041.91"));
		result.setTotalInterest(new BigDecimal("41.40"));

		MonthlyTrans monthlyTrans = InstallmentService.calcualteMonthlyTransection(new BigDecimal("1000.51"), 6,
				new BigDecimal("0.69"));
		
		
		Assert.assertTrue(result.getFirstPayment().equals(monthlyTrans.getFirstPayment()));
		Assert.assertTrue(monthlyTrans.getTotalAmt().equals(result.getTotalAmt()));
		
	}

}
