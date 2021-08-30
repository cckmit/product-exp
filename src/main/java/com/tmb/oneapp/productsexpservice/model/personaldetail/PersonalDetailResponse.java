package com.tmb.oneapp.productsexpservice.model.personaldetail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PersonalDetailResponse {
    private String prefix;
    private String residentStatus;
    private String citizenId;
    private String engName;
    private String engSurname;
    private String thaiName;
    private String  thaiSurname;
    private String email;
    private Calendar birthDate;
    private String idIssueCtry1;
    private Calendar expiryDate;
    private String nationality;
    private Address address;
    private String mobileNo;
    private List<DropDown> thaiSalutationCode;
    private List<DropDown> residentFlag;
}
