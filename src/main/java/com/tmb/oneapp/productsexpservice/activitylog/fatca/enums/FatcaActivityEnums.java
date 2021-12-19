package com.tmb.oneapp.productsexpservice.activitylog.fatca.enums;

import lombok.Getter;

@Getter
public enum FatcaActivityEnums {

    CLICK_NEXT_BUTTON_AT_FATCA_QUESTION_SCREEN("101000800", "MF complete FATCA Form", "After user press on Next button on FATCA question screen");

    private String activityTypeId;
    private String description;
    private String event;

    FatcaActivityEnums(String activityTypeId, String description, String event) {
        this.activityTypeId = activityTypeId;
        this.description = description;
        this.event = event;
    }
}
