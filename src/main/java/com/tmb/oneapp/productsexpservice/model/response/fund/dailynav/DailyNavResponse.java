package com.tmb.oneapp.productsexpservice.model.response.fund.dailynav;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNavResponse {

    private Status status;

    private DailyNavBody data;
}
