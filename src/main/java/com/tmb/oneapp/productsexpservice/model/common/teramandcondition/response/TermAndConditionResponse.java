package com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermAndConditionResponse {

    private Status status;

    private TermAndConditionResponseBody data;
}
