package com.github.twitch.app.core;

import java.io.File;

public final class CoreConstants {
	public static final String STREAMS_PATH = System.getProperty("user.home")
			+ File.separator + "TwitchApplication" + File.separator + "streams.tmp";
	public static final File STREAMS_FILE = new File(STREAMS_PATH);

	public static final String REDIRECT_URI = "http://www.twitch.tv/";
	public static final String CLIENT_ID = "9aur2chywu66ku3swvb8z4f56j90s5n";
	public static final String CLIENT_SECRET = "7cfdumqfev8cei2cpgom9uj6v3te3eu";

	private CoreConstants() {

	}
}
