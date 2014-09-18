package com.github.twitch.app.common;

import java.io.File;

public final class AppPropertiesConstants {
	public static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")
			+ File.separator + "TwitchApplication";
	public static final File PROPERTIES_FILE = new File(PROPERTIES_FILE_PATH + File.separator + "app.props");

	public static final String LIVESTREAMER_PATH = "livestreamer.path";
	public static final String STREAM_PANEL_SIZE = "stream.panel.size";
	public static final String STREAM_PANEL_SHOW = "stream.panel.show";

	private AppPropertiesConstants() {
	}

}
