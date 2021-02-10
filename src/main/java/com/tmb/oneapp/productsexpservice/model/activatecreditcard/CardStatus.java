package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

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
public class CardStatus {
	private Integer stopCode;
	private String stopCodeDesc;
	private String accountStatus;
	private String accountAgreeStatus;
	private String activatedDate;
	private String blockCode;
	private String previousExpiryDate;
	private String applicationType;
	private String stopDate;
	private String cardPloanFlag;
	private String cardActiveFlag;
	private String cardRole;
}
