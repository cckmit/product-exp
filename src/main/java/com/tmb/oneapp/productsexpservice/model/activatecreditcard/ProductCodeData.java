package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCodeData {
	@JsonProperty("product_name_th")
	private String productNameTH;
	@JsonProperty("product_name_en")
	private String productNameEN;
	@JsonProperty("icon_id")
	private String iconId;

}
