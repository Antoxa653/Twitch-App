package com.github.twitch.app.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoreController {
	private static Logger logger = LoggerFactory.getLogger(CoreController.class);

	private CoreController() {
	}

	public static void updateStreamStore() {
		Map<String, Stream> streamStore = StreamStore.getStreamsStore();
		Collection<Stream> streamStoreValues = streamStore.values();
		for (Stream stream : streamStoreValues) {
			new Thread(new StreamStoreUpdater(stream)).start();
		}
	}

	public static void initStreamStore() {
		StreamStore.init(CoreConstants.STREAMS_FILE);
	}

	public static Map<String, Stream> getStreamStore(String mode) {
		Map<String, Stream> customStreamStoreMap;
		switch (mode) {
		case "ALL":
			customStreamStoreMap = StreamStore.getStreamsStore();
			break;
		case "ACTIVE":
			customStreamStoreMap = StreamStore.getActiveStreamsStore();
			break;
		case "UNACTIVE":
			customStreamStoreMap = StreamStore.getInactiveStreamsStore();
			break;
		default:
			customStreamStoreMap = StreamStore.getStreamsStore();
			break;
		}
		return customStreamStoreMap;
	}

	public static void removeStream(String streamName) {
		StreamStore.removeStream(streamName);
	}

	public static void addStream(String streamName, Stream stream) {
		StreamStore.addStream(streamName, stream);
	}

	public static Stream getStream(String streamName) {
		return StreamStore.getStream(streamName);
	}

	public static void saveStreamStore() {
		StreamStore.save(CoreConstants.STREAMS_FILE);
	}

	public static void startStream(Stream stream, String quality) {
		StreamStarter.start(stream, quality);
	}

	public static boolean checkUrl(String url) {
		URL newUrl = null;
		try {
			newUrl = new URL(url);
		} catch (MalformedURLException e) {
			logger.error("Invalide URL");
		}
		return newUrl != null;

	}

}
