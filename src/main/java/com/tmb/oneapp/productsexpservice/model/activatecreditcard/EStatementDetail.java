package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.common.model.StatementFlag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EStatementDetail {
	@JsonProperty("email_address")
	private String emailAddress;
	@JsonProperty("email_verify_flag")
	private String emailVerifyFlag;
	private StatementFlag statementFlag;
}
