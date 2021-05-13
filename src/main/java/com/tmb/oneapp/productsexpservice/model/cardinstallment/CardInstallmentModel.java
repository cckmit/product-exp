package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "amounts", "order_no" })
@Data
public class CardInstallmentModel {
	@JsonProperty("amounts")
	private Double amounts;
	@JsonProperty("order_no")
	private String orderNo;
	@JsonProperty("transaction_description")
	private String transactionDescription;
	@JsonProperty("transaction_key")
	private String transactionKey;
	@JsonProperty("transection_date")
	private String transectionDate;
	@JsonProperty("post_date")
	private String postDate;
}
