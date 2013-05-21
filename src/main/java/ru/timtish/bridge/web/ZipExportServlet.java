package ru.timtish.bridge.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.web.util.UrlConstants;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Component("zipExportServlet")
public class ZipExportServlet implements HttpRequestHandler {

	@Autowired
	private StreamsBox streamsBox;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> streamKeyList;
		String keys = request.getParameter(UrlConstants.PARAM_KEYS);
		if (keys != null) {
			streamKeyList = Arrays.asList(keys.split(","));
		} else {
			streamKeyList = new ArrayList<String>();
		}

		// todo: check permissions

		String zipName = request.getParameter("box");

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(zipName, "UTF-8") + "\"");
		response.addCookie(new Cookie("fileDownload", "true"));

		ZipOutputStream zipStream = new ZipOutputStream(response.getOutputStream());
		try {
			for (String key : streamKeyList) {
				AbstractStream stream = streamsBox.getStream(key);

				if (stream == null) continue; // todo: show warning

				ZipEntry zipEntry = new ZipEntry(stream.getName());
				zipEntry.setSize(stream.getSize());
				zipEntry.setComment(stream.getDescription());
				zipStream.putNextEntry(zipEntry);

				stream.write(zipStream);

				if (!stream.isRepeatable()) {
					streamsBox.release(key);
				}
			}
		} finally {
			zipStream.close();
		}
	}

}
