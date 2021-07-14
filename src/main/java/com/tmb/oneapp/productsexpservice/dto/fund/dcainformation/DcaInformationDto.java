package com.tmb.oneapp.productsexpservice.dto.fund.dcainformation;

import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcaInformationDto {
    List<FundClassListInfo> fundClassList;
}
