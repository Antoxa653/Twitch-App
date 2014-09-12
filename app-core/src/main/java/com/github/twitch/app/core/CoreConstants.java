package com.github.twitch.app.core;

import java.io.File;

public final class CoreConstants {
	public static final String STREAMS_PATH = System.getProperty("user.home")
			+ File.separator + "TwitchApplication" + File.separator + "streams";
	public static final File STREAMS_FILE = new File(STREAMS_PATH);

	private CoreConstants() {

	}
}
