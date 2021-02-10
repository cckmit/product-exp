package com.tmb.oneapp.productsexpservice.model.portdata;

import com.tmb.oneapp.productsexpservice.model.DataStatus;
import lombok.Data;



@Data
public class PortResponseData {
    private DataStatus status;
    private PortData data;
}
