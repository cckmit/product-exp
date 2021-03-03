package com.tmb.oneapp.productsexpservice.model.response.buildstatement;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BilledStatementResponse {
    private SilverlakeStatus status;
    private CardStatement cardStatement;
    private Integer totalRecords;
    private Integer maxRecords;
    private String moreRecords;
    private String searchKeys;
}
