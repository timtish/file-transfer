package ru.timtish.bridge.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
@Controller
public class MultiPartImportServlet {

	@Autowired
	private StreamsBox streamsBox;

	@RequestMapping(value = "/put_mp", method = RequestMethod.POST)
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
					long size = file.getSize();
					AbstractStream stream = new AbstractStream(file.getInputStream(), size >= 0 ? size : null,
							BoxUtil.safeFileName(file.getName()), request.getRemoteUser(), description);
					stream.setRepeatable(stream.getSize() < 100 * 1024 * 1024); // todo: from runtime settings
					stream.setContentType(file.getContentType());
					streamsBox.addStreams(key, stream);
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
			response.sendRedirect("box.html?" + UrlConstants.PARAM_NEW_KEYS + "=" + newKeys);
		}
	}

}
