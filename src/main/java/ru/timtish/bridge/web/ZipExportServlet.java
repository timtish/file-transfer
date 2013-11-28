package ru.timtish.bridge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.box.BoxFile;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.web.util.UrlConstants;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Component("zipExportServlet")
public class ZipExportServlet implements HttpRequestHandler {

	@Autowired
	private StreamsBox streamsBox;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> fileNameList;
		String keys = request.getParameter(UrlConstants.PARAM_KEYS);
		if (keys != null) {
			fileNameList = Arrays.asList(keys.split(","));
		} else {
			fileNameList = new ArrayList<String>();
		}

		// todo: check permissions

		String zipName = streamsBox.getRoot().getName() + ".zip";

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(zipName, "UTF-8") + "\"");
		response.addCookie(new Cookie("fileDownload", "true"));

		ZipOutputStream zipStream = new ZipOutputStream(response.getOutputStream());
		try {
			for (String fileName : fileNameList) {
                if (fileName.startsWith(File.separator)) fileName = fileName.substring(1);
                if (fileName.startsWith("root" + File.separatorChar)) {
                    fileName = fileName.substring("root".length() + 1);
                }
				BoxEntity entity = streamsBox.getRoot().getChild(fileName);

				if (!(entity instanceof BoxFile)) continue; // todo: show warning

				AbstractStream stream = ((BoxFile) entity).getInputStream();

				ZipEntry zipEntry = new ZipEntry(stream.getName());
				zipEntry.setSize(stream.getSize());
				zipEntry.setComment(stream.getDescription());
				zipStream.putNextEntry(zipEntry);

				stream.write(zipStream);

				if (!stream.isRepeatable()) {
					streamsBox.release(fileName);
				}
			}
		} finally {
			zipStream.close();
		}
	}

}
