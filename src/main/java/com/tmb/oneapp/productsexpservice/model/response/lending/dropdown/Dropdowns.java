package com.tmb.oneapp.productsexpservice.model.response.lending.dropdown;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Dropdowns {

    private Dropdowns() {

    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmploymentStatus {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class RmOccupation {
        private String code;
        private String name;
        private String name2;
        private List<Occupation> occupation;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Occupation {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BusinessType {
        private String code;
        private String name;
        private String name2;
        private List<BusinessSubType> businessSubType;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BusinessSubType {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class IncomeBank {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class IncomeType {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class SciCountry {
        private String code;
        private String name;
        private String name2;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class TotalIncome {
        private BigDecimal min;
        private BigDecimal max;
    }

}
