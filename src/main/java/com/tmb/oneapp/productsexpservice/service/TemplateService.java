package com.tmb.oneapp.productsexpservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.tmb.common.logger.TMBLogger;

@Service
public class TemplateService {

    private TemplateEngine templateEngine;

    @Autowired
    public TemplateService(TemplateEngine engine) {
        this.templateEngine = engine;
    }

    private static final TMBLogger<TemplateService> logger = new TMBLogger<>(TemplateService.class);

    public String getHtmlContent(String templateFile, Context ctx) {
        return templateEngine.process(templateFile, ctx);
    }

    public String getSoGoodItemTh(Context ctx) {
        return getHtmlContent("html/applysogood_th.html", ctx);
    }

    public String getSoGoodItemEn(Context ctx) {
        return getHtmlContent("html/applysogood_en.html", ctx);

    }

}
