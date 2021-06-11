package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.CardInstallment;

import lombok.Data;

@Data
public class CreditCardModel {

	@JsonProperty("account_id")
	private String accountId;
	@JsonProperty("card_installment")
	private CardInstallment cardInstallment;
}
