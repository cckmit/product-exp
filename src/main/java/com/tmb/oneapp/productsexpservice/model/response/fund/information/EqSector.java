package com.tmb.oneapp.productsexpservice.model.response.fund.information;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EqSector {

    private String basic;

    private String communication;

    private String cyclical;

    private String defensive;

    private String energy;

    private String financial;

    private String health;

    private String industrial;

    private String realestate;

    private String tech;

    private String utilities;
}
