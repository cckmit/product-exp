package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreditCardDetail {
	private String cardId;
	private String accountId;
	private String productId;
	private Long directDepositAccount;
	private String directDepositBank;
	private SilverlakeCustomerDetail customer;
	private CardInfo cardInfo;
	private List<CardPhone> cardPhones;
	private CardAddresses cardAddresses;
	private CardEmail cardEmail;
	private BalancePoints balancePoints;
	private BalancePoints cardPoints;
	private CardStatus cardStatus;
	private CardBalances cardBalances;
	private CardCreditLimit cardCreditLimit;
	private SilverlakeCustomerDetail customerSupp;
	private CardCashAdvance cardCashAdvance;
}
