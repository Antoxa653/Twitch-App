package com.github.twitch.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppProperties {
	private static Logger logger = LoggerFactory.getLogger(AppProperties.class);
	private static Properties appProperties;

	private AppProperties(Properties properties) {
		if (properties.isEmpty() && !propertiesFileExist(AppPropertiesConstants.PROPERTIES_FILE)) {
			appProperties = loadDefaultProperties();
			saveProperties(AppPropertiesConstants.PROPERTIES_FILE);
		}
		else {
			appProperties = properties;
		}
	}

	public static AppProperties loadProperties(File propertiesFile) {
		Properties properties = new Properties();
		if (propertiesFile != null && propertiesFile.exists() && propertiesFile.isFile()) {
			try (FileInputStream propertiesStream = new FileInputStream(propertiesFile)) {
				properties.load(propertiesStream);
			} catch (IOException e) {
				logger.error("Error while reading properties file", propertiesFile.toString(), e);
			}
		}
		return new AppProperties(properties);
	}

	public static void saveProperties(File propertiesFile) {
		if (propertiesFile != null && !propertiesFile.exists()) {
			propertiesFile.getParentFile().mkdirs();
		}
		try (FileOutputStream propertiesStream = new FileOutputStream(propertiesFile)) {
			appProperties.store(propertiesStream, "TwitchApp properties file");
		} catch (IOException e) {
			logger.error("Error while writing properties file", propertiesFile.toString(), e);
		}
	}

	public static String getValue(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key must be not null");
		}
		if (!appProperties.containsKey(key)) {
			throw new IllegalArgumentException("properties doesn't contains key such this: " + key);
		}
		return appProperties.getProperty(key);
	}

	public static void setValue(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key must be not null");
		}
		if (!appProperties.containsKey(key)) {
			throw new IllegalArgumentException("properties doesn't contains key such this: " + key);
		}
		appProperties.put(key, value);

	}

	public static void setValueAndSaveProperties(File propertiesFile, String key, String value) {
		setValue(key, value);
		saveProperties(propertiesFile);
	}

	private final Properties loadDefaultProperties() {
		Properties properties = new Properties();
		properties.put(AppPropertiesConstants.LIVESTREAMER_PATH, "C:\\");
		properties.put(AppPropertiesConstants.STREAM_PANEL_SHOW, "ALL");
		properties.put(AppPropertiesConstants.STREAM_PANEL_SIZE, "MEDIUM");
		return properties;
	}

	private final boolean propertiesFileExist(File file) {
		return file.exists();
	}

}
