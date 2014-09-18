package com.giithub.twitch.app.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch.app.common.AppProperties;
import com.github.twitch.app.common.AppPropertiesConstants;

public class AppPropertiesTest {
	private File testPropertiesFile;
	private static Logger logger = LoggerFactory.getLogger(AppPropertiesTest.class);

	@Before
	public void init() {
		testPropertiesFile = new File("src/test/resources/testProperties.props");
		createTestPropertiesFile();
	}

	@After
	public void clear() {
		testPropertiesFile.deleteOnExit();
	}

	@Test
	public void loadPropertiesTest() {
		AppProperties appProperties = AppProperties.initProperties(testPropertiesFile);
		Assert.assertEquals("C:\\", appProperties.getValue(AppPropertiesConstants.LIVESTREAMER_PATH));
		Assert.assertEquals("ALL", appProperties.getValue(AppPropertiesConstants.STREAM_PANEL_SHOW));
		Assert.assertEquals("MEDIUM", appProperties.getValue(AppPropertiesConstants.STREAM_PANEL_SIZE));
	}

	@Test
	public void savePropertiesTest() {
		AppProperties appProperties = AppProperties.initProperties(testPropertiesFile);		
		appProperties.setValue(AppPropertiesConstants.LIVESTREAMER_PATH, "D:\\");
		appProperties.setValue(AppPropertiesConstants.STREAM_PANEL_SHOW, "OFFLINE");
		appProperties.setValue(AppPropertiesConstants.STREAM_PANEL_SIZE, "HIGH");
		appProperties.saveProperties(testPropertiesFile);
		AppProperties.initProperties(testPropertiesFile);
		Assert.assertEquals("D:\\", appProperties.getValue(AppPropertiesConstants.LIVESTREAMER_PATH));
		Assert.assertEquals("OFFLINE", appProperties.getValue(AppPropertiesConstants.STREAM_PANEL_SHOW));
		Assert.assertEquals("HIGH", appProperties.getValue(AppPropertiesConstants.STREAM_PANEL_SIZE));
	}

	private void createTestPropertiesFile() {
		Properties testProperties = new Properties();
		testProperties.put(AppPropertiesConstants.LIVESTREAMER_PATH, "C:\\");
		testProperties.put(AppPropertiesConstants.STREAM_PANEL_SHOW, "ALL");
		testProperties.put(AppPropertiesConstants.STREAM_PANEL_SIZE, "MEDIUM");
		try (FileOutputStream out = new FileOutputStream(testPropertiesFile)) {
			testProperties.store(out, "Properties file for testing");
		} catch (FileNotFoundException e) {
			logger.error("File " + testPropertiesFile + "cause file not found exception", e);
		} catch (IOException e) {
			logger.error("File " + testPropertiesFile + "cause IOException while writing in", e);
		}
	}
}
