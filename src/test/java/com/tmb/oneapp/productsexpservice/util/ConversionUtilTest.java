package com.tmb.oneapp.productsexpservice.util;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

@RunWith(JUnit4.class)
public class ConversionUtilTest {

    @Test
    public void testStringToDouble() {
        Double result = ConversionUtil.stringToDouble("10.0");
        Assertions.assertEquals(10.0, result);
    }

    @Test
    public void testBigDecimalToDouble() {
        Double result = ConversionUtil.bigDecimalToDouble(new BigDecimal(1000000.00));
        Assertions.assertEquals(1000000.00, result);
    }

    @Test
    public void testDoubleToString() {
        String result = ConversionUtil.doubleToString(Double.valueOf(100));
        Assert.assertEquals("100.0", result);
    }
}

