package com.tmb.oneapp.productsexpservice.util;

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

    /**
     * @param value
     * @return
     */
    public static String doubleToString(Double value) {
      return  Double.toString(value);
    }
}
