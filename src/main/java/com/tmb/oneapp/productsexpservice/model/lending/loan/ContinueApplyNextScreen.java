package com.tmb.oneapp.productsexpservice.model.lending.loan;

import lombok.Getter;

@Getter
public enum ContinueApplyNextScreen {

    PERSONAL,
    WORKING,
    INCOME,
    UPLOAD_DOC,

    CASH_TRANSFER_DAY1,
    FINAL_APPROVE_LOAN_CONFIRMATION,
    RESULT,
    CONFIRMATION,
    UNKNOWN;

 }
