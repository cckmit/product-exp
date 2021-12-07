package com.tmb.oneapp.productsexpservice.enums;

import lombok.Getter;

@Getter
public enum ActivityLogStatus {

    COMPLETED("Completed"),
    FAILED("Failed");

    String status;

    ActivityLogStatus(String status) {
        this.status = status;
    }
}
