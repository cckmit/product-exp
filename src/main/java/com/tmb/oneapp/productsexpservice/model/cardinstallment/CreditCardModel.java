package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
	@JsonPropertyOrder({
	"account_id",
	"card_installment"
	})
    @Data
	public class CreditCardModel {

	@JsonProperty("account_id")
	private String accountId;
	@JsonProperty("card_installment")
	private CardInstallmentModel cardInstallment;
}