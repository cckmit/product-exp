package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardInstallmentConfirmResponse {

	@JsonProperty("credit_confirm_response")
	List<CardInstallmentResponse> confirmRespList;

}
