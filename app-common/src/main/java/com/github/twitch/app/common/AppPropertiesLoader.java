package com.github.twitch.app.common;

public final class AppPropertiesLoader {
	private static AppPropertiesLoader instance;
	private static AppProperties properties;

	private AppPropertiesLoader() {
		properties = AppProperties.loadProperties(AppPropertiesConstants.PROPERTIES_FILE);
	}

	public static AppPropertiesLoader getInstance() {
		if (instance == null) {
			instance = new AppPropertiesLoader();
		}
		return instance;
	}

	public AppProperties getAppProperties() {
		return properties;
	}

}