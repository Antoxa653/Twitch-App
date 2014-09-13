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

public final class StreamStore {
	private static Logger logger = LoggerFactory.getLogger(StreamStore.class);
	private static Map<String, Stream> store;

	private StreamStore(Map<String, Stream> streams) {
		if (!streams.isEmpty()) {
			store = streams;
		}
		else {
			store = new HashMap<String, Stream>();
		}
	}

	public static StreamStore init(File streamsFile) {
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

	public static void save(File streamsFile) {
		try (FileOutputStream out = new FileOutputStream(streamsFile);
				ObjectOutputStream objStream = new ObjectOutputStream(out)) {
			objStream.writeObject(store);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException while trying to save " + streamsFile, e);
		} catch (IOException e) {
			logger.error("IOException while trying to save" + streamsFile, e);
		}
	}

	public static Stream getStream(String streamName) {
		if (streamName == null && !store.containsKey(streamName)) {
			throw new IllegalArgumentException("StreamsStore doesn't contain such key as: " + streamName);
		}
		else {
			return store.get(streamName);
		}
	}

	public static void addStream(String streamName, Stream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Stream instance must not be null: " + stream);
		}
		if (streamName == null) {
			throw new IllegalArgumentException("streamName  must not be null: " + stream);
		}
		else {
			store.put(streamName, stream);
		}
	}

	public static void removeStream(String streamName) {
		if (streamName == null) {
			throw new IllegalArgumentException("streamName must not be null: " + streamName);
		}
		if (!store.containsKey(streamName)) {
			throw new IllegalArgumentException("StreamsStore doesn't contain such key as: " + streamName);
		}
		else {
			store.remove(streamName);
		}
	}

	public static void removeAll() {
		store.clear();
	}

	public static void updateStream(String streamName) {
		if (streamName == null && !store.containsKey(streamName)) {
			throw new IllegalArgumentException("StreamsStore doesn't contain such key as: " + streamName);
		}
		else {
			//StreamUpdater.update(getStream(streamName).getUrl());
		}
	}

	public static Map<String, Stream> getStore() {
		return store;
	}

}
