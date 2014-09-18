package com.github.twitch.app.core;

import java.util.Map;

public final class CoreController {

	private CoreController() {
	}

	public static void updateStreamStore() {
		StreamsStore.update();
	}

	public static void initStreamStore() {
		StreamsStore.init(CoreConstants.STREAMS_FILE);
	}

	public static Map<String, Stream> getStreamStore(String mode) {
		return StreamsStore.getStreamsStore(mode);
	}

	public static void removeStream(String streamName) {
		StreamsStore.removeStream(streamName);
	}

	public static void addStream(String streamName, Stream stream) {
		StreamsStore.addStream(streamName, stream);
	}

	public static Stream getStream(String streamName) {
		return StreamsStore.getStream(streamName);
	}

	public static void saveStreamStore() {
		StreamsStore.save(CoreConstants.STREAMS_FILE);
	}

	public static void startStream(String key, Stream stream, String quality) {
		StreamStarter.start(key, stream, quality);
	}

	public static boolean checkUrl(String url) {
		return Stream.checkUrl(url);

	}	

}
