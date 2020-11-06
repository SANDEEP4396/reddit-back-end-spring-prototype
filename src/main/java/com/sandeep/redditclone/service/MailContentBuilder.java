package com.sandeep.redditclone.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
class MailContentBuilder {

    private final TemplateEngine templateEngine;

    String build(String message) {
        //Email message is set inside thymleaf context object
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
    }
}
