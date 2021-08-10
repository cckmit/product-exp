package com.tmb.oneapp.productsexpservice.model.response.lending.dropdown;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DropdownsLoanSubmissionWorkingDetail {
    private List<Dropdowns.EmploymentStatus> employmentStatus;
    private List<Dropdowns.RmOccupation> rmOccupation;
    private List<Dropdowns.BusinessType> businessType;
    private List<Dropdowns.TotalIncome> totalIncome;
    private List<Dropdowns.IncomeBank> incomeBank;
    private List<Dropdowns.IncomeType> incomeType;
    private List<Dropdowns.SciCountry> sciCountry;
    private List<String> cardDelivery;
    private List<String> emailStatementFlag;
}
