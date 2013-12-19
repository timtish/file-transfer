package ru.timtish.bridge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Service
public class MailService {

	/*@Qualifier("mailserver")
	@Autowired
	private JamesMailServer james;*/

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String address, String title, String body) {
		//System.out.println(james.getHelloName() + " domain: " + james.getDefaultDomain() + " email to " + address);

		/*
		todo:
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(address);
		mail.setSubject(title);
		mail.setText(body);
		mailSender.send(mail);*/
	}

}
