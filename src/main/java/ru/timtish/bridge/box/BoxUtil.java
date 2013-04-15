package ru.timtish.bridge.box;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class BoxUtil {

	public static BoxEntity findEntity(BoxEntity dir, String fileName) {

		if (fileName == null) return dir;
		if (fileName.startsWith("/")) fileName = fileName.substring(1);
		if (fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length() - 1);
		if (fileName.isEmpty()) return dir;

		for (BoxEntity entity : dir.getChilds()) {
			System.out.println("= " + dir.getName() + " + " + entity.getName() + " ? " + fileName);
			if (fileName.equals(entity.getName())) return entity;
			// todo: @BlackMagicDetected
			//if (entity instanceof BoxZipFile && (dir.getName() + "/" + fileName).equals(entity.getName())) return entity;
			//if (entity instanceof BoxZipFile && fileName.equals("/" + dir.getName() + "/" + entity.getName())) return entity;
			if (entity.isContainer() && fileName.startsWith(entity.getName() + "/")) {
				return findEntity(entity, fileName.substring(entity.getName().length() + 1));
			}
		}

		return null;
	}

}
