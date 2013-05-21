package ru.timtish.bridge.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import ru.timtish.bridge.services.MailService;
import ru.timtish.bridge.web.util.UrlConstants;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Component("mailExportServlet")
public class MailServlet implements HttpRequestHandler {

	@Autowired
	private MailService mailService;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> streamKeyList;
		String keys = request.getParameter(UrlConstants.PARAM_KEYS);
		if (keys != null) {
			streamKeyList = Arrays.asList(keys.split(","));
		} else {
			streamKeyList = new ArrayList<String>();
		}
		// TODO: implement
		mailService.sendMail(request.getParameter("to"), "mail from file bridge", request.getParameter("body"));
	}


}
