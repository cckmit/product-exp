package com.tmb.oneapp.productsexpservice.model.fund.information.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationBody {

    private Information fund;
}
