package com.tmb.oneapp.productsexpservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SoGoodInfo {

	private double interestRate;
	private Integer tenor;
	private String transactionDes;
	private BigDecimal totolInterest;
	private BigDecimal totolAmt;
	private LocalDateTime transectionDate;
	private LocalDate postDate;

}
