package com.tmb.oneapp.productsexpservice.model.applyestatement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class StatementFlag {
	
	@JsonProperty("e_consolidate_statement_flag")
	private String eConsolidateStatementFlag;
	@JsonProperty("e_creditcard_statement_flag")
	private String eCreditcardStatementFlag;
	@JsonProperty("e_ready_cash_statement_flag")
	private String eReadyCashStatementFlag;
	@JsonProperty("e_cash_to_go_statement_flag")
	private String eCashToGoStatementFlag;
	@JsonProperty("w_consolidate_statement_flag")
	private String wConsolidateStatementFlag;
	@JsonProperty("e_lending_statement_flag")
	private String eLendingStatementFlag;
	@JsonProperty("e_mutual_fund_statement_flag")
	private String eMutualFundStatementFlag;
	@JsonProperty("e_bancassurance_statement_flag")
	private String eBancassuranceStatementFlag;
	private String createDate;
	private String createBy;
	private String updateDate;
	private String updateBy;
	private String channelName;
	
}
