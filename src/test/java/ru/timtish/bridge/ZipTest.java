package ru.timtish.bridge;

import org.junit.Test;
import ru.timtish.bridge.box.BoxEntity;
import ru.timtish.bridge.pipeline.AbstractStream;
import ru.timtish.bridge.pipeline.converter.Zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

            AbstractStream stream = new AbstractStream(zip, (Long) null, null, null, null);
            stream.setRepeatable(true); // todo: test not repeatable - event for event driven logic
            List<BoxEntity> files = Zip.getFilesTree(stream);
            assertEquals(1, files.get(0).getChilds().size());
            assertEquals(2, files.size());

        } finally {
            zip.close();
        }
    }

}
