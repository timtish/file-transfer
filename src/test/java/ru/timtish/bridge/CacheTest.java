package ru.timtish.bridge;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import ru.timtish.bridge.pipeline.cache.CacheInitializer;
import ru.timtish.bridge.pipeline.AbstractStream;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class CacheTest {

	@Test
	public void testRepeatableMemoryCache() {
		AbstractStream stream = new AbstractStream(new TestInputStream(100), 100L, "testStream", "user", "description");
		stream.setRepeatable(true);
		new CacheInitializer(stream).run();
		try {
			stream.write(new TestOutputStream());
			stream.write(new TestOutputStream());
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		Assert.assertTrue(stream.createCopy() instanceof ByteArrayInputStream);

	}

	@Test
	public void testRepeatableFileCache() {
		long size = 10 * 1024 * 1024;
		AbstractStream stream = new AbstractStream(new TestInputStream(size), size, "testStream", "user", "description");
		stream.setRepeatable(true);
		try {
			long time = System.currentTimeMillis();
			stream.write(new TestOutputStream());
			System.out.println("time: " + (System.currentTimeMillis() - time));
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testNonRepeatableSmallStream() {
		AbstractStream stream = new AbstractStream(new TestInputStream(100), 100L, "testStream", "user", "description");
		stream.setRepeatable(false);
		new CacheInitializer(stream).run();

		try {
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		try {
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (IllegalStateException e) {
			Assert.assertEquals("Input stream already read, lose 100 bytes", e.getLocalizedMessage());
		}
	}

	@Test
	public void testNonRepeatableBigStream() {
		long size = 10 * 1024 * 1024;
		AbstractStream stream = new AbstractStream(new TestInputStream(size), size, "testStream", "user", "description");
		stream.setRepeatable(false);

		try {
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		try {
			stream.write(new TestOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (IllegalStateException e) {
			Assert.assertEquals("Input stream already read, lose " + size + " bytes", e.getLocalizedMessage());
		}
	}

}
