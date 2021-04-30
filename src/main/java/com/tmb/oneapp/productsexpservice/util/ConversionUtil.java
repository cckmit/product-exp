package com.tmb.oneapp.productsexpservice.util;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class ConversionUtil {

	private ConversionUtil() {
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static Double stringToDouble(String value) {
		if (StringUtils.isEmpty(value)) {
			return 0d;
		}
		return Double.valueOf(value);

	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static Double bigDecimalToDouble(BigDecimal value) {
		if (Objects.isNull(value)) {
			return 0d;
		}
		return value.doubleValue();
	}

	/**
	 * @param value
	 * @return
	 */
	public static String doubleToString(Double value) {
		if (Objects.isNull(value)) {
			return StringUtils.EMPTY;
		}
		return Double.toString(value);
	}
}
