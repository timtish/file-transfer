package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class UrlImportServlet {

	private static final String PARAM_URL = "url";
	private static final String PARAM_DESCRIPTION = "description";

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

        UrlResource res = new UrlResource(url);
        String key = BoxUtil.wrapStream(streamsBox, res, null, url, null, request.getRemoteUser(),                description, CacheInitializer.CacheType.IN_NEW_THREAD);
		response.sendRedirect("box.html?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
	}

}
