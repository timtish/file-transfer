package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static ru.timtish.bridge.pipeline.cache.CacheInitializer.CacheType.IN_NEW_THREAD;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
@RequestMapping(value = "/txt")
public class UrlEncodedImportServlet {

    private static final Logger LOG = Logger.getLogger(UrlImportServlet.class.getName());
    private static final int BIG_LENGTH = 1 * 1024 * 1024;

	@Autowired
	private StreamsBox streamsBox;

	@RequestMapping(method = RequestMethod.POST)
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = request.getParameter("data");
		if (data == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data not found in request");
            return;
        }

        byte[] buffer = data.getBytes();
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) name = "txt";
        String key = BoxUtil.wrapStream(streamsBox, new ByteArrayResource(buffer), (long) buffer.length, name,
                "text/plan", request.getRemoteUser(), request.getParameter("description"), IN_NEW_THREAD);
        response.sendRedirect("box.html?" + UrlConstants.PARAM_NEW_KEYS + "=" + key);
	}

    @RequestMapping(method = RequestMethod.GET)
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
