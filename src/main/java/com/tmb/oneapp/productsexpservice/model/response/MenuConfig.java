package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * This class is the menu in native mobile app
 * @author Admin
 *
 */
@Setter
@Getter
@NoArgsConstructor
public class MenuConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NonNull
	@Id
	@ApiModelProperty(notes = "code to map functionality to menu onclicks", required = true)
	private String code;
	
	@ApiModelProperty(notes = "This property used to enable/disable menu items", required = true)
	@JsonProperty("is_enable")
	private boolean isEnabled;
	
	@ApiModelProperty(notes = "text for menu items : it just for reference no where using in UI", required = true)
	private String description;
	
	@ApiModelProperty(notes = "channel", required = true)
	private String channel;
	
	@ApiModelProperty(notes = "based on this key menu order will rearrange", required = true)
	private int order;

	@ApiModelProperty(notes = "text to map with phases menu", required = true)
	private String title;
}