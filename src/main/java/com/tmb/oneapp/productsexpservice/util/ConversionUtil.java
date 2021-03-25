package com.tmb.oneapp.productsexpservice.util;

import java.math.BigDecimal;

public class ConversionUtil {
	
    private ConversionUtil() {
    }

    /**
     * @param string
     * @return
     */
    public static Double stringToDouble(String string) {
        return Double.valueOf(string);

    }

    public static Double bigDecimalToDouble(BigDecimal value) {
        return value.doubleValue();

    }


    /**
     * @param value
     * @return
     */
    public static String doubleToString(Double value) {
      return  Double.toString(value);
    }
}
