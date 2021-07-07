package com.tmb.oneapp.productsexpservice.model.applyestatement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonPropertyOrder({ "cc_id", "rm_id", "cust_type_id", "tha_fname", "eng_fname", "tha_lname", "eng_lname", "id_card",
		"birth_date", "entry_branch", "update_branch", "entry_date", "entry_by", "last_update", "update_by" })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Profile {

	@JsonProperty("cc_id")
	public String ccId;
	@JsonProperty("rm_id")
	public String rmId;
	@JsonProperty("cust_type_id")
	public String custTypeId;
	@JsonProperty("tha_fname")
	public String thaFname;
	@JsonProperty("eng_fname")
	public String engFname;
	@JsonProperty("tha_lname")
	public String thaLname;
	@JsonProperty("eng_lname")
	public String engLname;
	@JsonProperty("id_card")
	public String idCard;
	@JsonProperty("birth_date")
	public String birthDate;
	@JsonProperty("entry_branch")
	public String entryBranch;
	@JsonProperty("update_branch")
	public String updateBranch;
	@JsonProperty("entry_date")
	public String entryDate;
	@JsonProperty("entry_by")
	public String entryBy;
	@JsonProperty("last_update")
	public String lastUpdate;
	@JsonProperty("update_by")
	public String updateBy;

}