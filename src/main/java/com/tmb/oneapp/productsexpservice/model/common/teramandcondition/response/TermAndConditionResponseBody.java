package com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermAndConditionResponseBody {

    private String id;

    @JsonAlias({"service_term_and_condition_id"})
    private String serviceTermAndConditionId;

    private String status;

    private String version;

    @JsonAlias({"version_display"})
    private String versionDisplay;

    @JsonAlias({"service_code"})
    private String serviceCode;

    private String channel;

    @JsonAlias({"service_name_en"})
    private String serviceEnglishNme;

    @JsonAlias({"service_name_th"})
    private String serviceThaiName;

    @JsonAlias({"service_term_and_condition_description"})
    private String serviceTermAndConditionDescription;

    @JsonAlias({"create_date"})
    private String createDate;

    @JsonAlias({"update_date"})
    private String updateDate;

    @JsonAlias({"update_by"})
    private String updateBy;

    @JsonAlias({"create_by"})
    private String createBy;

    @JsonAlias({"html_th"})
    private String thaiHtml;

    @JsonAlias({"html_en"})
    private String englishHtml;

    @JsonAlias({"pdf_link"})
    private String pdfLink;
}
