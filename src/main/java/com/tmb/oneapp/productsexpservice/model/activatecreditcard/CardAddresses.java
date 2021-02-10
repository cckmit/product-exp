package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardAddresses {
	private String city;
	private String postalCode;

	@JsonProperty("address_1")
	private String address1;

	@JsonProperty("address_2")
	private String address2;

	@JsonProperty("address_3")
	private String address3;

	private String country;
	private String stateCode;
	private String stateTxt;
}
