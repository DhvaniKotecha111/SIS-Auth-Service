package com.opl.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired 
	private JavaMailSender javaMailSender;
	
	public String sendOtpEmail(String to, String subject, String otp) throws MessagingException, IOException {
		// Load the HTML template from resources directory
		String templatePath = "otp_email_template.html";
		String emailTemplate = loadTemplate(templatePath);

        // Replace the OTP placeholder with the actual OTP value
        String emailContent = emailTemplate.replace("{{OTP}}", otp);
        
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(emailContent, true);
		javaMailSender.send(message);
		
		return "OTP Sent Successfully on registered Email Id";
	}
	
	public String sendResetLink(String to, String subject, String link) throws MessagingException, IOException {
		// Load the HTML template from resources directory
		String templatePath = "reset_password_link.html";
		String emailTemplate = loadTemplate(templatePath);

        // Replace the OTP placeholder with the actual OTP value
        String emailContent = emailTemplate.replace("{{LINK}}", link);
        
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(emailContent, true);
		javaMailSender.send(message);
		
		return "Link to Reset Password sent successfully on registered Email Id";
	}
	
	// Method to load HTML template from the classpath
	public String loadTemplate(String templatePath) throws IOException {
	    // Read the contents of the HTML template file
	    return new String(Files.readAllBytes(Paths.get("src/main/resources/templates", templatePath)));
	}

}
