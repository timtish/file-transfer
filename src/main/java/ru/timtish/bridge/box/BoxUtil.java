package ru.timtish.bridge.box;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class BoxUtil {

	private static final Map<String, BoxDirectory> dirs = new HashMap<String, BoxDirectory>();

	public static BoxEntity getBoxEntity(String user, String box, String path) {
		// todo: check security, find user directory
		BoxDirectory dir = findById(box);
		return  dir == null ? null : dir.getChild(path);
	}

	public static String safeFileName(String fileName) {
		if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		if (fileName.contains("\\")) fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		fileName = fileName.replace("[\\W?%=]", "_");
		return fileName;
	}

	public static String getContentTypeIcon(String contentType) {
		if (contentType == null) return "bin";
		if (contentType.startsWith("text")) return "txt";
		if (contentType.startsWith("image")) return "img";
		if (contentType.equals("application/zip")) return "zip";
		if (contentType.startsWith("application/x-")) return "exe";
		if (contentType.startsWith("application")) return "doc";
		return "bin";
	}

	public static String getId(BoxEntity entity) {
		if (entity == null) return "";
		return getId(entity.getParent()) + File.separator + entity.getName();
	}

	public static BoxDirectory findById(String boxId) {
		return dirs.get(boxId);
	}

	public static void addBox(String id, BoxDirectory dir) {
		dirs.put(id, dir);
	}
}
