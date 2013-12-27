package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class SimpleStreamServlet {

	@Autowired
	private StreamsBox streamsBox;

    @RequestMapping(value = "/put_file", method = RequestMethod.POST)
	protected ModelAndView doPost(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AbstractStream stream = new AbstractStream(request.getInputStream(), request.getContentLength(),
				BoxUtil.safeFileName(request.getHeader("X-FILE-NAME")), request.getRemoteUser(), null);
		stream.setContentType(request.getContentType());
		stream.setRepeatable(stream.getSize() < 1024 * 1024);
		new CacheInitializer(stream).run();
        String key = streamsBox.addStreams(stream);

		response.getOutputStream().write(key.getBytes());
        return new ModelAndView("/index.xhtml", Collections.singletonMap("key", key));
	}

    @RequestMapping(value = "/get_file", method = RequestMethod.GET)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter(UrlConstants.PARAM_KEY);

		// todo: check permissions
        //String sslID = (String)request.getAttribute("javax.servlet.request.ssl_session");


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
			streamsBox.release(stream.getId());
		}
	}
}
