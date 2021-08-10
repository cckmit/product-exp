package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum RslResponseCodeEnum implements Serializable {
	SUCCESS("MSG_000", "Success");
	
	private final String code;
	private final String message;
}
