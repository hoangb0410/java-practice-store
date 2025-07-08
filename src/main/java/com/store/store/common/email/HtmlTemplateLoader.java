package com.store.store.common.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class HtmlTemplateLoader {

    public String loadOtpTemplate(String otp) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/otp-template.html");
        String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return template.replace("{{otp}}", otp);
    }
}
