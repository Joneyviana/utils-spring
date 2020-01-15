package com.utils.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    public JavaMailSender emailSender;
	
	public void sendSimpleMessage(
		      String to, String subject, String text) {
                SimpleMailMessage message = new SimpleMailMessage(); 
                message.setFrom("contato.modelocei@gmail.com");
                message.setTo(to); 
		        message.setSubject(subject); 
		        message.setText(text);
		        emailSender.send(message);
		    }
	
	public void sendLoginPasswordEmail(String email,String password) {
		sendSimpleMessage(email,
       			"Informe de senha","Este é seu login: "+email+"\n"
       	+"Esta é sua senha: "+password);
	}
}
