package ru.timtish.bridge.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import ru.timtish.bridge.services.MailService;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MailServlet extends HttpServlet {

	@Autowired
	private MailService mailService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doSendMail(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doSendMail(req, resp);
	}

	private void doSendMail(HttpServletRequest req, HttpServletResponse resp) {
		// TODO: implement
		mailService.sendMail(req.getParameter("to"), "mail from file bridge", req.getParameter("body"));
	}


}
