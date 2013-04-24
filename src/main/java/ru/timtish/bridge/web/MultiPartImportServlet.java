package ru.timtish.bridge.web;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.timtish.bridge.box.BoxUtil;
import ru.timtish.bridge.box.StreamsBox;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.web.util.UrlConstants;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class MultiPartImportServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newKeys = "";

		// multipart/mixed application/octet-stream

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart)
		{
			List<FileItem> items;
			String description = null;
			try {
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				//factory.setSizeThreshold(1000000);
				//factory.setRepository(ConfigServlet.getTempDirectory());
				items = new ServletFileUpload(factory).parseRequest(request);
			} catch (FileUploadException e) {
				throw new ServletException("Cannot parse multipart request.", e);
			}

			for (FileItem item : items) {
				if (item.isFormField()) {
					// Process regular form field (input type="text|radio|checkbox|etc", select, etc).
					String fieldname = item.getFieldName();
					String fieldvalue = item.getString("UTF-8");

					if ("description".equalsIgnoreCase(fieldname)) description = fieldvalue;
				}
			}

			for (FileItem file : items) {
				if (!file.isFormField()) {
					String key = UUID.randomUUID().toString();
					AbstractStream stream = new AbstractStream(file.getInputStream(), file.getSize(),
							BoxUtil.safeFileName(file.getName()), request.getRemoteUser(), description);
					stream.setRepeatable(stream.getSize() < 1024 * 1024);
					stream.setContentType(file.getContentType());
					StreamsBox.getInstance().addStreams(key, stream);
					if (file.isInMemory()) {
						new Thread(new CacheInitializer(stream)).start();
					}
					if (!newKeys.isEmpty()) newKeys +=",";
					newKeys += key;
				}
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Stream not is multipart");
			return;
		}

		if (newKeys.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Stream not found in request");
		} else {
			response.sendRedirect("box.jsp?" + UrlConstants.PARAM_NEW_KEYS + "=" + newKeys);
		}
	}

}
