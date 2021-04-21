package com.tmb.oneapp.productsexpservice.model;

import java.util.List;

import lombok.Data;

@Data
public class SoGoodWrapper {

	private String interestRatePercent;
	private String tenor;
	private List<SoGoodItemInfo> items;

}
