package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.FAILED_MESSAGE;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.SUCCESS_MESSAGE;

@Getter
@AllArgsConstructor
public enum LegacyResponseCodeEnum implements Serializable {
	SUCCESS("MSG_000", "Success");
	
	private final String code;
	private final String message;
}
