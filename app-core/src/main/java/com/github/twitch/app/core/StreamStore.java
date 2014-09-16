package com.github.twitch.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class StreamStore {
	private static Logger logger = LoggerFactory.getLogger(StreamStore.class);
	private static Map<String, Stream> streamsStore;
	private static Map<String, Stream> activeStreamsStore;
	private static Map<String, Stream> inactiveStreamsStore;

	private StreamStore(Map<String, Stream> streams) {
		setUpStreamsStores(streams);
	}

	@SuppressWarnings("unchecked")
	static StreamStore init(File streamsFile) {
		Map<String, Stream> streams = new HashMap<String, Stream>();
		if (streamsFile != null && streamsFile.exists() && streamsFile.isFile()) {
			try (FileInputStream in = new FileInputStream(streamsFile);
					ObjectInputStream objStream = new ObjectInputStream(in)) {
				streams = (HashMap<String, Stream>) objStream.readObject();
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException while trying to read " + streamsFile, e);
			} catch (IOException e) {
				logger.error("IOException while trying to read" + streamsFile, e);
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException while casting Object to HashMap<String, Stream>", e);
			}
		}
		return new StreamStore(streams);
	}

	static void save(File streamsFile) {
		try (FileOutputStream out = new FileOutputStream(streamsFile);
				ObjectOutputStream objStream = new ObjectOutputStream(out)) {
			objStream.writeObject(streamsStore);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException while trying to save " + streamsFile, e);
		} catch (IOException e) {
			logger.error("IOException while trying to save" + streamsFile, e);
		}
	}

	static Stream getStream(String streamName) {
		if (streamName == null && !streamsStore.containsKey(streamName)) {
			throw new IllegalArgumentException("StreamsStore doesn't contain such key as: " + streamName);
		}
		else {
			return streamsStore.get(streamName);
		}
	}

	static void addStream(String streamName, Stream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Stream instance must not be null: " + stream);
		}
		if (streamName == null) {
			throw new IllegalArgumentException("streamName  must not be null: " + stream);
		}
		else {
			streamsStore.put(streamName, stream);
		}
	}

	static void removeStream(String streamName) {
		if (streamName == null) {
			throw new IllegalArgumentException("streamName must not be null: " + streamName);
		}
		if (!streamsStore.containsKey(streamName)) {
			throw new IllegalArgumentException("StreamsStore doesn't contain such key as: " + streamName);
		}
		else {
			streamsStore.remove(streamName);
		}
	}

	static void removeAll() {
		streamsStore.clear();
	}

	static Map<String, Stream> getStreamsStore() {
		return streamsStore;
	}

	static Map<String, Stream> getActiveStreamsStore() {
		return activeStreamsStore;
	}

	static Map<String, Stream> getInactiveStreamsStore() {
		return inactiveStreamsStore;
	}

	void setUpStreamsStores(Map<String, Stream> streams) {
		if (!streams.isEmpty()) {
			activeStreamsStore = new HashMap<String, Stream>();
			inactiveStreamsStore = new HashMap<String, Stream>();
			streamsStore = streams;
			for (String key : streamsStore.keySet()) {
				if (streamsStore.get(key).isActive()) {
					activeStreamsStore.put(key, streamsStore.get(key));
				}
				else {
					inactiveStreamsStore.put(key, streamsStore.get(key));
				}
			}

		}
		else {
			streamsStore = new HashMap<String, Stream>();
			activeStreamsStore = new HashMap<String, Stream>();
			inactiveStreamsStore = new HashMap<String, Stream>();
		}
	}
}
