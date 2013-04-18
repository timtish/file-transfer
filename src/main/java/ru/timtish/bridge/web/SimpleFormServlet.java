package ru.timtish.bridge.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.box.StreamsBox;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class SimpleFormServlet extends javax.servlet.http.HttpServlet {

	private static final Logger LOG = Logger.getLogger(SimpleFormServlet.class.getName());

	protected void doPost(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = request.getParameter("data");
		if (data != null) {
			byte[] buffer = data.getBytes();
			String key = UUID.randomUUID().toString();
			AbstractStream stream = new AbstractStream(new ByteArrayInputStream(buffer), buffer.length,
					BoxUtil.safeFileName(request.getParameter("name")), request.getRemoteUser(), request.getParameter("description"));
			stream.setRepeatable(buffer.length < 1024 * 1024);
			stream.setContentType("text/plan");
			StreamsBox.getInstance().addStreams(key, stream);
			new Thread(new CacheInitializer(stream)).start();

			response.sendRedirect("box.jsp?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data not found in request");
		}
	}

	protected void doGet(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter(UrlConstants.PARAM_KEY);

		// todo: check permissions

		AbstractStream stream = StreamsBox.getInstance().getStream(key);
		if (stream == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Stream \"" + key + "\" not found");
			return;
		}

		if (stream.getSize() != null) {
			response.setContentLength(stream.getSize().intValue());
		}

		stream.write(response.getOutputStream());
		response.getOutputStream().close();

		if (!stream.isRepeatable()) {
			StreamsBox.getInstance().release(key);
		}
	}
}
