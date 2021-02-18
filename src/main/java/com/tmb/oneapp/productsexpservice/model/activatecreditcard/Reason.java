package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reason {

	private String reasonCode;

	private String reasonEn;

	private String reasonTh;

}
