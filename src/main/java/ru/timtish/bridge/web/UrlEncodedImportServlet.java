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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class UrlEncodedImportServlet {

	@Autowired
	private StreamsBox streamsBox;

	@RequestMapping(value = "/put_txt", method = RequestMethod.POST)
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = request.getParameter("data");
		if (data != null) {
			byte[] buffer = data.getBytes();
			String key = UUID.randomUUID().toString();
			String name = request.getParameter("name");
			if (name == null || name.trim().isEmpty()) name = "txt";
			name = BoxUtil.safeFileName(name);
			AbstractStream stream = new AbstractStream(new ByteArrayInputStream(buffer), buffer.length, name, request.getRemoteUser(), request.getParameter("description"));
			stream.setRepeatable(buffer.length < 1024 * 1024);
			stream.setContentType("text/plan");
			streamsBox.addStreams(key, stream);
			new Thread(new CacheInitializer(stream)).start();

			response.sendRedirect("box.html?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data not found in request");
		}
	}

}
