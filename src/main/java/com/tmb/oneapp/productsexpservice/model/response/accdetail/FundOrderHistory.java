package com.tmb.oneapp.productsexpservice.model.response.accdetail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundOrderHistory extends StatementList {

}
