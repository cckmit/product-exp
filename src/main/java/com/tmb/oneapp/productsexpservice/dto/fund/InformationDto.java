package com.tmb.oneapp.productsexpservice.dto.fund;

import com.tmb.oneapp.productsexpservice.model.response.fund.dailynav.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.response.fund.information.InformationBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationDto {

    private InformationBody information;

    private DailyNavBody dailyNav;
}
