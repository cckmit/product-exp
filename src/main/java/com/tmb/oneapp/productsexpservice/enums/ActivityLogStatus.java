package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum ActivityLogStatus {

    FAILURE("Failure"),
    SUCCESS("Success"),
    COMPLETED("Completed"),
    FAILED("Failed");

    String status;

    ActivityLogStatus(String status) {
        this.status = status;
    }
}
