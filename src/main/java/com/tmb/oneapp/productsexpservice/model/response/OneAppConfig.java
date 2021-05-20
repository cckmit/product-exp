package com.tmb.oneapp.productsexpservice.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OneAppConfig {

    @ApiModelProperty(notes = "Unique key to store App config in mongo DB", required = true)
    private String id;

    @ApiModelProperty(notes = "Channel name {ex :mb}", required = true)
    private String channel;

    @ApiModelProperty(notes = "It contains Key value pairs of app configuration", required = true)
    private HashMap<String, String> details;

    @ApiModelProperty(notes = "It contains Key value pairs of images url used in One app", required = true)
    private HashMap<String, String> image_urls;

    @ApiModelProperty(notes = "It contains menu details", required = true)
    private List<MenuConfig> menu;

    @ApiModelProperty(notes = "It contains th/en phrases content", required = true)
    private HashMap<String, HashMap<String, String>> phrases;

    @ApiModelProperty(notes = "firebase analytics configuration", required = true)
    @JsonProperty("firebase_event_config")
    private List<FireBaseConfigData> fireBaseConfig;

    @JsonProperty("setting_config")
    private SettingConfig settingConfig;

//    @JsonProperty("lending_module")
//    private AllowCashDayOne allowCashDayOne;
}