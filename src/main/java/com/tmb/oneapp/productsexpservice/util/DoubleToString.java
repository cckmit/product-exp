package com.tmb.oneapp.productsexpservice.util;

public class DoubleToString {
    private DoubleToString() {
    }

    public static Double stringToDouble(String string) {
        return Double.valueOf(string);

    }

    public static String doubleToString(Double value) {
      return  Double.toString(value);
    }
}
