package com.tmb.oneapp.productsexpservice.mapper.dcainformation;


import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationModel;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DcaInformationMapper {

    @Mapping(target = "portfolioNumber", ignore = true)
    @Mapping(target = "unrealizedProfit", ignore = true)
    @Mapping(target = "unrealizedProfitPercent", ignore = true)
    @Mapping(target = "marketValue", ignore = true)
    DcaInformationModel fundClassInfoToDcaInformationModel(FundClassListInfo fundClassListInfo);
}
