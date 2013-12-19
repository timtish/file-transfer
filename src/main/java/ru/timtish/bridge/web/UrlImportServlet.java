package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class UrlImportServlet {

	private static final Logger LOG = Logger.getLogger(UrlImportServlet.class.getName());

	private static final String PARAM_URL = "url";
	private static final String PARAM_DESCRIPTION = "description";

	private static final int BIG_LENGTH = 1 * 1024 * 1024;

	@Autowired
	private StreamsBox streamsBox;

    @RequestMapping(value = "/put_st", method = RequestMethod.POST)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter(PARAM_URL);
		String description = request.getParameter(PARAM_DESCRIPTION);
		if (url == null || url.trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Resource url can't be empty");
			return;
		}

		URL source = new URL(url);
		URLConnection connection = source.openConnection();
		String key = UUID.randomUUID().toString();
		AbstractStream stream = new AbstractStream(connection.getInputStream(), connection.getContentLength(), BoxUtil.safeFileName(url), request.getRemoteUser(), description);
		stream.setRepeatable(stream.getSize() < 1024 * 1024);
		stream.setContentType(connection.getContentType());

		streamsBox.addStreams(key, stream);
		new Thread(new CacheInitializer(stream)).start();

		response.sendRedirect("box.html?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
	}

    @RequestMapping(value = "/get_big", method = RequestMethod.GET)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentLength(BIG_LENGTH);
		for (int i = 0; i < BIG_LENGTH; i++) {
			try {
				response.getOutputStream().write(i);
				if (i % 1024 == 0) LOG.finest("big> " + i);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
			if (i % 1024 == 0) try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOG.finest("big> finish");
	}

}
