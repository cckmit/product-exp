package com.tmb.oneapp.productsexpservice.constant;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * enum class for response code to maintain response status
 *
 */
@Getter
@AllArgsConstructor
public enum ResponseCode implements Serializable {
	SUCESS("0000", "success", Constants.SERVICE_NAME, "success"),
	GENERAL_ERROR("0001", "general error", Constants.SERVICE_NAME, "unknown error"),
	FAILED("0001", "failed", Constants.SERVICE_NAME, "failed"),
	DATA_NOT_FOUND_ERROR("0009", "DATA NOT FOUND", Constants.SERVICE_NAME, "DATA NOT FOUND");

	private String code;
	private String message;
	private String service;
	private String desc;

	private static class Constants {
		public static final String SERVICE_NAME = "products-exp-service";
	}
}