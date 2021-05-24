package com.tmb.oneapp.productsexpservice.model.response;

//import com.tmb.common.model.Section;
//import com.tmb.common.model.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class SettingConfig {
//    private List<Section> sections;
//    private List<Setting> settings;
    private List<AllowCashDayOne> allowCashDayOnes;
}
