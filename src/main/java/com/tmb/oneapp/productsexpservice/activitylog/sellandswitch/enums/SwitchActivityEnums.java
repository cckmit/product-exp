package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.enums;

import lombok.Getter;

@Getter
public enum SwitchActivityEnums {

    NEXT_BUTTON_AT_SWITCHING_SCREEN("101000401", "Switch fund", "Click Next button at Switching screen"),
    ACCEPT_TERM_AND_CONDITION("101000402", "Switch fund", "At confirmation screen when already accepted T&C, Risk and click confirm button"),
    ENTER_PIN_IS_CORRECT("101000403", "Switch fund", "Enter PIN is correct then display transaction details completed");

    private String activityTypeId;
    private String description;
    private String event;

    SwitchActivityEnums(String activityTypeId, String description, String event) {
        this.activityTypeId = activityTypeId;
        this.description = description;
        this.event = event;
    }
}
