package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.timtish.bridge.services.MailService;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class MailServlet {

	@Autowired
	private MailService mailService;

	@RequestMapping(value = "/mail", method = RequestMethod.GET)
	public void handleRequest(@RequestParam(UrlConstants.PARAM_KEYS) String keys,
                              @RequestParam("to") String to,
                              @RequestParam("body") String body) throws ServletException, IOException {
		List<String> streamKeyList;
		if (keys != null) {
			streamKeyList = Arrays.asList(keys.split(","));
		} else {
			streamKeyList = new ArrayList<String>();
		}
		// TODO: implement
		mailService.sendMail(to, "mail from file bridge", body);
	}


}
