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
public class CardInfo {
	private Long billingCycle;
	private String cardEmbossingName1;
	private String cardEmbossingName2;
	private String issuedBy;
	private String expiredBy;
	private String lastUpdateDate;
	private String effectiveDate;
	private String createdDate;
}
