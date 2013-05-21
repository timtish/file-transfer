package ru.timtish.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.converter.Zip;

import static org.junit.Assert.assertEquals;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class ZipTest {

	@Test
	public void getZipFiles() throws IOException {
		InputStream zip = ClassLoader.getSystemResource("test.zip").openStream();
		try {

			if (System.getProperty("sun.zip.encoding") == null) System.setProperty("sun.zip.encoding", "CP866");

			List<BoxEntity> files = Zip.getFilesTree(new AbstractStream(zip, (Long) null, null, null, null));
			assertEquals(files.size(), 2);
			assertEquals(files.get(0).getChilds().size(), 1);

		} finally {
			zip.close();
		}
	}

}
