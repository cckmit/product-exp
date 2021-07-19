package com.tmb.oneapp.productsexpservice.mapper.dcainformation;


import com.tmb.oneapp.productsexpservice.dto.fund.dcainformation.DcaInformationModel;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DcaInformationMapper {
    DcaInformationModel fundClassInfoToDcaInformationModel(FundClassListInfo fundClassListInfo);
}
