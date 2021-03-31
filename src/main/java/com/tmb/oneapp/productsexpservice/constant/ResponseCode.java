package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.FAILED_MESSAGE;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.SUCCESS_MESSAGE;

/**
 * enum class for response code to maintain response status
 *
 */
@Getter
@AllArgsConstructor
public enum ResponseCode implements Serializable {
	SUCESS("0000", SUCCESS_MESSAGE, Constants.SERVICE_NAME, SUCCESS_MESSAGE),
	GENERAL_ERROR("0001", "general error", Constants.SERVICE_NAME, "unknown error"),
	FAILED("0001", FAILED_MESSAGE, Constants.SERVICE_NAME, FAILED_MESSAGE),
	DATA_NOT_FOUND_ERROR("0009", "DATA NOT FOUND", Constants.SERVICE_NAME, "DATA NOT FOUND"),
	HP_RSL_SUCCESS("AST_0000", SUCCESS_MESSAGE, Constants.SERVICE_NAME, SUCCESS_MESSAGE),
	HP_RSL_DATA_NOT_FOUND("AST_0001", "data not found", Constants.SERVICE_NAME, "success"),
	HP_RSL_ERROR("AST_0004", "HP and RSL error", Constants.SERVICE_NAME, FAILED_MESSAGE),
	HP_ERROR_CODE("AST_0003", "HP error", Constants.SERVICE_NAME, FAILED_MESSAGE),
	RSL_ERROR_CODE("AST_0002", "RSL error", Constants.SERVICE_NAME, FAILED_MESSAGE);

	private String code;
	private String message;
	private String service;
	private String desc;

	private static class Constants {
		public static final String SERVICE_NAME = "products-exp-service";
	}
}