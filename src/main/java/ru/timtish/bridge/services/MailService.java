package ru.timtish.bridge.services;

import org.apache.james.JamesMailServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Service
public class MailService {

	@Qualifier("mailserver")
	@Autowired
	private JamesMailServer james;

	public void sendMail(String address, String title, String body) {
		System.out.println(james.getHelloName() + " domain: " + james.getDefaultDomain());
	}

}
