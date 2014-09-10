package com.github.twitch.app;

import java.io.File;

public final class AppPropertiesConstants {
	public static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")
			+ File.separator + "TwitchApplication" + File.separator + "app.props";
	public static final File PROPERTIES_FILE = new File(PROPERTIES_FILE_PATH);

	public static final String LIVESTREAMER_PATH = "livestreamer.path";
	public static final String STREAM_PANEL_SIZE = "stream.panel.size";
	public static final String STREAM_PANEL_SHOW = "stream.panel.show";

	private AppPropertiesConstants() {
	}

}
