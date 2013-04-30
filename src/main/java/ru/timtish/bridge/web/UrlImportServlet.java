package ru.timtish.bridge.web;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.web.util.UrlConstants;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class UrlImportServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(UrlImportServlet.class.getName());

	private static final String PARAM_URL = "url";
	private static final String PARAM_DESCRIPTION = "description";

	private static final int BIG_LENGTH = 1 * 1024 * 1024;

	@Autowired
	private StreamsBox streamsBox;

	@Override
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

		response.sendRedirect("box.jsp?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
	}

	@Override
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
