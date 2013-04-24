package ru.timtish.bridge.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class SimpleStreamServlet extends HttpServlet {

	protected void doPost(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = UUID.randomUUID().toString();
		AbstractStream stream = new AbstractStream(request.getInputStream(), request.getContentLength(),
				BoxUtil.safeFileName(request.getHeader("X-FILE-NAME")), request.getRemoteUser(), null);
		stream.setContentType(request.getContentType());
		stream.setRepeatable(stream.getSize() < 1024 * 1024);
		new CacheInitializer(stream).run();
		StreamsBox.getInstance().addStreams(key, stream);

		response.getOutputStream().write(key.getBytes());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		if (stream.getContentType() != null) {
			response.setContentType(stream.getContentType());
		}
		if (stream.getName() != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(stream.getName(), "UTF-8") + "\"");
		}
		response.addCookie(new Cookie("fileDownload", "true"));

		stream.write(response.getOutputStream());

		response.getOutputStream().close();

		if (!stream.isRepeatable()) {
			StreamsBox.getInstance().release(key);
		}
	}
}
