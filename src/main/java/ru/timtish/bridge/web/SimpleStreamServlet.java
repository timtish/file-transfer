package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import ru.timtish.bridge.box.*;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Component("simpleStreamServlet")
public class SimpleStreamServlet implements HttpRequestHandler {

	@Autowired
	private StreamsBox streamsBox;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ("get".equalsIgnoreCase(request.getMethod())) {
			doGet(request, response);
		} else {
			doPost(request, response);
		}
	}

	protected void doPost(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = UUID.randomUUID().toString();
		AbstractStream stream = new AbstractStream(request.getInputStream(), request.getContentLength(),
				BoxUtil.safeFileName(request.getHeader("X-FILE-NAME")), request.getRemoteUser(), null);
		stream.setContentType(request.getContentType());
		stream.setRepeatable(stream.getSize() < 1024 * 1024);
		new CacheInitializer(stream).run();
		streamsBox.addStreams(key, stream);

		response.getOutputStream().write(key.getBytes());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter(UrlConstants.PARAM_KEY);

		// todo: check permissions

        /*BoxEntity box = BoxUtil.findById(key);
        AbstractStream stream = null;
        if (box instanceof BoxFile) stream = ((BoxFile) box).getInputStream();
        if (box instanceof BoxZipFile) stream = ((BoxZipFile) box).getInputStream();
        //if (box instanceof BoxDirectory) stream = ((BoxDirectory) box).getInputStream(); // todo: zip directory   */
        AbstractStream stream = streamsBox.getStream(key);

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
			streamsBox.release(BoxUtil.findStreamKey(streamsBox, stream));
		}
	}
}
