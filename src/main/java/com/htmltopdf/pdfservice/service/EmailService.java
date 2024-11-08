package com.htmltopdf.pdfservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);

	  @Value("${spring.mail.username}")
	  private String mailUsername;
	    @Autowired
	    private JavaMailSender mailSender;

	    public void sendSimpleEmail(String toEmail, String subject, String body) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(mailUsername);
	        message.setTo(toEmail);
	        message.setSubject(subject);
	        message.setText(body);

	        mailSender.send(message);
	        LOGGER.info("Mail sent successfully to " + toEmail);
	        System.out.println("Mail sent successfully to " + toEmail);
	    }
	
	
}
