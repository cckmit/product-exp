package com.tmb.oneapp.productsexpservice.model.response.stmtresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementResponse {

    private String totalRecord;

    private List<StatementList> statementList;
}
