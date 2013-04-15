package ru.timtish.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.pipeline.converter.Zip;

import static org.junit.Assert.*;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class ZipTest {

	@Test
	public void getZipFiles() throws IOException {
		InputStream zip = ClassLoader.getSystemResource("test.zip").openStream();
		try {

			List<BoxEntity> files = Zip.getFilesTree(zip, "");
			assertEquals(files.size(), 2);
			assertEquals(files.get(0).getChilds().size(), 1);

		} finally {
			zip.close();
		}
	}

}
