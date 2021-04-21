package com.tmb.oneapp.productsexpservice.model;

import java.util.List;

import lombok.Data;

public class SoGoodWrapper {
	
	private String interestRatePercent;
	private String tenor;
	private List<SoGoodItemInfo> items;
	public String getInterestRatePercent() {
		return interestRatePercent;
	}
	public void setInterestRatePercent(String interestRatePercent) {
		this.interestRatePercent = interestRatePercent;
	}
	public String getTenor() {
		return tenor;
	}
	public void setTenor(String tenor) {
		this.tenor = tenor;
	}
	public List<SoGoodItemInfo> getItems() {
		return items;
	}
	public void setItems(List<SoGoodItemInfo> items) {
		this.items = items;
	}
	
	
	
}
