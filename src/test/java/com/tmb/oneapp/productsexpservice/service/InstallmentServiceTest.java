package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.MonthlyTrans;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

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
    
    @Test
    void testCalculationCase2() {
    	MonthlyTrans monthlyTrans = InstallmentService.calcualteMonthlyTransection(new BigDecimal("1041.461"), 6,
                new BigDecimal("0.69"));
    	
    	MonthlyTrans result = new MonthlyTrans();
        result.setFirstPayment(new BigDecimal("180.79"));
        result.setInterest(new BigDecimal("7.18607"));
        result.setInterest2Digit(new BigDecimal("7.18000"));
        result.setPrinciple(new BigDecimal("173.57667"));
        result.setPrinciple2Digit(new BigDecimal("173.57000"));
        result.setTotalAmt(new BigDecimal("1084.54"));
        result.setTotalInterest(new BigDecimal("43.11644"));

        Assert.assertTrue(result.getFirstPayment().equals(monthlyTrans.getFirstPayment()));
        Assert.assertTrue(monthlyTrans.getTotalAmt().equals(result.getTotalAmt()));
    	
    }

}
