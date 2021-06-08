package com.tmb.oneapp.productsexpservice.model.request.notification;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Calendar;

@Setter
@Getter
public class ReportRequest {
    private BigDecimal appRefNo;
    private String productName;
    private String customerName;
    private String surname;
    private BigDecimal cardID;
    private BigDecimal finalLoanAmount;
    private BigDecimal interestRate;
    private BigDecimal cashDisbursement;
    private String accountReceive;
    private BigDecimal currentLoan;
    private BigDecimal currentAccount;
    private String tenor;
    private Calendar applyDate;
    private Calendar dueDate;
    private Calendar firstPayDate;
    private Calendar nextDueDate;
    private BigDecimal installment;
    private String paymentMethod;
    private String email;
    private String botAnswer1;
    private String botAnswer2;
    private Calendar consentDate;
    private Calendar consentTime;
    private String flagValue;
}
