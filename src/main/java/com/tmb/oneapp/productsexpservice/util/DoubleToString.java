package com.tmb.oneapp.productsexpservice.util;

public class DoubleToString {

    public static Double stringToDouble(String string) {
        double num = Double.valueOf(string);
        return num;
    }

    public static String doubleToString(Double value) {
        String num = Double.toString(value);
        return num;
    }
}
