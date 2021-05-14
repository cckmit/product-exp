package com.tmb.oneapp.productsexpservice.model.loan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Preload {
    private int crmId;
    private int appId;

    public Preload(int crmId, int appId) {
        this.crmId = crmId;
        this.appId = appId;
    }
}
