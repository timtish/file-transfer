package ru.timtish.bridge.pipeline.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ru.timtish.bridge.box.BoxDirectory;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.box.BoxZipFile;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.cache.CachedInMemoryInputStream;
import ru.timtish.bridge.pipeline.cache.RepeatableInputStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class Zip {

	static {
		// fix for windows archives
		if (System.getProperty("sun.zip.encoding") == null) System.setProperty("sun.zip.encoding", "CP866");
	}

	public static void addToZip(AbstractStream in, ZipOutputStream stream) throws IOException {
		ZipEntry zipEntity = new ZipEntry(in.getName());
		if (in.getSize() != null) {
			zipEntity.setSize(in.getSize());
		}
		stream.putNextEntry(zipEntity);
		in.write(stream);
		stream.closeEntry();
	}

	private static List<BoxEntity> getChilds(ZipInputStream zip, String zipStreamKey) throws IOException {
		List<BoxEntity> list = new ArrayList<BoxEntity>();
		ZipEntry zipFile;
		try {
			while ((zipFile = zip.getNextEntry()) != null) {
				String fileName = zipFile.getName();
				if (fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length() - 1);
				String filePath = "";
				if (fileName.contains("/")) {
					filePath = fileName.substring(0, fileName.lastIndexOf("/"));
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				}
				BoxEntity subfolder = findFolderForFile(list, filePath);

				BoxEntity entity;
				if (zipFile.isDirectory()) {
					entity = new BoxDirectory(fileName, new Date(zipFile.getTime()), new ArrayList<BoxEntity>(), subfolder);
				} else {
					entity = new BoxZipFile(zipStreamKey, zipFile.getName(), zipFile.getSize(), null);
				}

				if (subfolder != null) {
					subfolder.getChilds().add(entity);
				} else {
					list.add(entity);
				}
			}
		} catch (Exception e) { // todo: remove
			e.printStackTrace();
		}
		System.out.println(">files " + Arrays.toString(list.toArray()));
		return list;
	}

	private static BoxEntity findFolderForFile(List<BoxEntity> list, String path) {
		if (path.startsWith("/")) path = path.substring(1);
		if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
		String name;
		if (path.contains("/")) {
			name = path.substring(0, path.indexOf("/"));
			path = path.substring(path.indexOf("/"));
		} else {
			name = path;
			path = "";
		}
		for (BoxEntity folder : list) if (name.equals(folder.getName())) {
			if (folder.isContainer() && path.isEmpty()) {
				return folder;
			} else {
				return findFolderForFile(folder.getChilds(), path);
			}
		}
		return null;
	}

	public static List<BoxEntity> getFilesTree(InputStream inputStream, String zipStreamKey) throws IOException {
		ZipInputStream zip = new ZipInputStream(inputStream);
		try {
			return getChilds(zip, zipStreamKey);
		} finally {
			zip.close();
		}
	}

	public static InputStream unzip(InputStream inputStream, String path) throws IOException {
		ZipInputStream zip = new ZipInputStream(inputStream);
		RepeatableInputStream fileStream = null;
		try {
			ZipEntry zipFile;
			while ((zipFile = zip.getNextEntry()) != null) {
				System.out.println(">zip: " + zipFile.getName() + " ? " + path);
				if (path.equals(zipFile.getName())) {
					if (!zipFile.isDirectory()) {
						long size = zipFile.getSize();
						fileStream = new CachedInMemoryInputStream(zip, size);
						return fileStream.createCopy();
					} else {
						throw new IllegalArgumentException(path + " is directory");
					}
				}
			}
			throw new IllegalArgumentException(path + " not found");
		} finally {
			if (fileStream != null) fileStream.clear();
			zip.close();
		}
	}
}
