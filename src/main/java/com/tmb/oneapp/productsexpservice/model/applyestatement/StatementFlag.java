package com.tmb.oneapp.productsexpservice.model.applyestatement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class StatementFlag {
	
	private String eConsolidateStatementFlag;
	private String eCreditcardStatementFlag;
	private String eReadyCashStatementFlag;
	private String eCashToGoStatementFlag;
	private String wConsolidateStatementFlag;
	private String eLendingStatementFlag;
	private String eMutualFundStatementFlag;
	private String eBancassuranceStatementFlag;
	private String createDate;
	private String createBy;
	private String updateDate;
	private String updateBy;
	private String channelName;
	
}
