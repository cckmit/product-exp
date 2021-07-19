package com.tmb.oneapp.productsexpservice.dto.fund.dcainformation;

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
    List<DcaInformationModel> fundClassList;
}
