package ru.timtish.bridge.box;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.UrlResource;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;

import java.io.File;
import java.io.IOException;
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

    public static String wrapStream(StreamsBox streamsBox, InputStreamSource s, Long size, String fileName, String contentType,
                              String owner, String description, CacheInitializer.CacheType cacheType) throws IOException {
        AbstractStream stream = new AbstractStream(s, size, BoxUtil.safeFileName(fileName), owner, description);
        stream.setContentType(contentType);
        stream.setRepeatable(isRepeatable(s) || size != null && size < 25 * 1024 * 1024);
        CacheInitializer.init(stream, cacheType);
        return streamsBox.addStreams(stream);
    }

    private static boolean isRepeatable(InputStreamSource s) {
        return s instanceof UrlResource || s instanceof ByteArrayResource;
    }

}
