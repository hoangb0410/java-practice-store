package com.store.store.common.email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final HtmlTemplateLoader htmlTemplateLoader;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.from-name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender, HtmlTemplateLoader htmlTemplateLoader) {
        this.mailSender = mailSender;
        this.htmlTemplateLoader = htmlTemplateLoader;
    }

    public void sendOTP(String to, String subject, String otp) {
        try {
            String htmlContent = htmlTemplateLoader.loadOtpTemplate(otp);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            ErrorHelper.internalServerError("Failed to send OTP email" + e.getMessage());
        }
    }
}
