package com.tmb.oneapp.productsexpservice.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ActivityLogStatus {

    SUCCESS("completed"),
    FAILED("Failed");

    String status;

    ActivityLogStatus(String status) {
        this.status = status;
    }
}
