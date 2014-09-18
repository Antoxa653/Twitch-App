package com.github.twitch.app.common;

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
		if (properties.isEmpty() && !AppPropertiesConstants.PROPERTIES_FILE.exists()) {
			logger.debug("--- Initialize AppProperties object with default application properties ---");
			appProperties = loadDefaultProperties();
			saveProperties(AppPropertiesConstants.PROPERTIES_FILE);
		}
		else {
			logger.debug("--- Initialize AppProperties object with properties have been read from file ---");
			appProperties = properties;
		}
	}

	public static AppProperties initProperties(File propertiesFile) {
		logger.debug("--- Initialize application settings ---");
		Properties properties = new Properties();
		if (propertiesFile != null && propertiesFile.exists() && propertiesFile.isFile()) {
			logger.debug("--- Reading application settings from file ---");
			try (FileInputStream propertiesStream = new FileInputStream(propertiesFile)) {
				properties.load(propertiesStream);
				logger.debug("--- Settings have been read from file ---");
			} catch (IOException e) {
				logger.error("Error while reading application settings from file :" + propertiesFile.toString(), e);
			}
		}
		return new AppProperties(properties);
	}

	public void saveProperties(File propertiesFile) {
		if (propertiesFile != null && !propertiesFile.exists()) {
			propertiesFile.getParentFile().mkdirs();
		}
		try (FileOutputStream propertiesStream = new FileOutputStream(propertiesFile)) {
			logger.debug("--- Saving AppProperties object to file ---");
			appProperties.store(propertiesStream, "TSV properties file");
		} catch (IOException e) {
			logger.error("Error while writing properties file", propertiesFile.toString(), e);
		}
	}

	public String getValue(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key must be not null");
		}
		if (!appProperties.containsKey(key)) {
			throw new IllegalArgumentException("properties doesn't contains key such this: " + key);
		}
		return appProperties.getProperty(key);
	}

	public void setValue(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("Key must not be null");
		}
		if (!appProperties.containsKey(key)) {
			throw new IllegalArgumentException("Properties doesn't contains key such this: " + key);
		}
		logger.debug("--- Setting propertie " + key + " with value = " + value);
		appProperties.put(key, value);

	}

	public void setValueAndSaveProperties(File propertiesFile, String key, String value) {
		setValue(key, value);
		saveProperties(propertiesFile);
	}

	public boolean checkLivestreamerPath(String path) {
		File propertiesFile = new File(path + File.separator + "livestreamer.exe");
		if (!propertiesFile.isFile() || !propertiesFile.canExecute()) {
			logger.error("Invalid livestreamer path");
			return false;
		}
		logger.debug("--- Current livestreamer path is correct ---");
		return true;

	}

	private final Properties loadDefaultProperties() {
		logger.debug("--- Initializing default application properties ---");
		Properties properties = new Properties();
		properties.put(AppPropertiesConstants.LIVESTREAMER_PATH, "C:\\Program Files (x86)\\livestreamer");
		properties.put(AppPropertiesConstants.STREAM_PANEL_SHOW, "ALL");
		properties.put(AppPropertiesConstants.STREAM_PANEL_SIZE, "SMALL");
		return properties;
	}
}
