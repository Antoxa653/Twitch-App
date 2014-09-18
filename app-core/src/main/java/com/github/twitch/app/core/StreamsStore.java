package com.github.twitch.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class StreamsStore {
	private static Logger logger = LoggerFactory.getLogger(StreamsStore.class);
	private static Map<String, Stream> streamsStore;
	private static Map<String, Stream> activeStreamsStore;
	private static Map<String, Stream> inactiveStreamsStore;

	private StreamsStore(Map<String, Stream> streams) {
		if (!streams.isEmpty()) {
			streamsStore = streams;
			update();
			setUpStreamsStores(streams);
		}
		else {
			logger.debug("--- StreamStore initialized by empty Stores ---");
			streamsStore = new HashMap<String, Stream>();
			activeStreamsStore = new HashMap<String, Stream>();
			inactiveStreamsStore = new HashMap<String, Stream>();
		}
	}

	@SuppressWarnings("unchecked")
	static StreamsStore init(File streamsFile) {
		logger.debug("--- Initialize StreamStore ---");
		Map<String, Stream> streams = new HashMap<String, Stream>();
		if (streamsFile != null && streamsFile.exists() && streamsFile.isFile()) {
			try (FileInputStream in = new FileInputStream(streamsFile);
					ObjectInputStream objStream = new ObjectInputStream(in)) {
				logger.debug("--- Reading StreamStore data from file ---");
				streams = (HashMap<String, Stream>) objStream.readObject();
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException while trying to read " + streamsFile, e);
			} catch (IOException e) {
				logger.error("IOException while trying to read" + streamsFile, e);
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException while casting Object to HashMap<String, Stream>", e);
			}
		}
		return new StreamsStore(streams);
	}

	static void save(File streamsFile) {
		logger.debug("---  Saving of the StreamStore began  ---");
		try (FileOutputStream out = new FileOutputStream(streamsFile);
				ObjectOutputStream objStream = new ObjectOutputStream(out)) {
			objStream.writeObject(streamsStore);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException while trying to save " + streamsFile, e);
		} catch (IOException e) {
			logger.error("IOException while trying to save" + streamsFile, e);
		}
		logger.debug("---  Saving of the StreamStore finished  ---");
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
			if (stream.isActive()) {
				activeStreamsStore.put(streamName, stream);
			}
			else {
				inactiveStreamsStore.put(streamName, stream);
			}
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
			activeStreamsStore.remove(streamName);
			inactiveStreamsStore.remove(streamName);
			save(CoreConstants.STREAMS_FILE);
		}
	}

	static void removeAll() {
		streamsStore.clear();
		activeStreamsStore.clear();
		inactiveStreamsStore.clear();

	}

	static Map<String, Stream> getStreamsStore(String mode) {
		switch (mode) {
		case "ALL":
			return streamsStore;
		case "ACTIVE":
			return activeStreamsStore;
		case "INACTIVE":
			return inactiveStreamsStore;
		default:
			return streamsStore;
		}
	}

	static void update() {
		logger.debug("--- Began the updaiting of StreamStore  ---");
		Queue<Stream> streamStoreValues = new LinkedList<Stream>();
		for (String stream : streamsStore.keySet()) {
			streamStoreValues.add(streamsStore.get(stream));
		}

		ExecutorService service = Executors.newFixedThreadPool(5);
		for (int i = 0; i < streamStoreValues.size(); i++) {
			service.submit(new Runnable() {

				@Override
				public void run() {
					streamStoreValues.poll().update();
				}
			});
		}
		service.shutdown();
		while (!service.isTerminated()) {

		}
		logger.debug("--- Finished the updaiting of StreamStore  ---");
		setUpStreamsStores(streamsStore);

	}

	private static void setUpStreamsStores(Map<String, Stream> streams) {
		logger.debug("---  activeStreamsStore & inactiveStreamsStore update started  ---");
		activeStreamsStore = new HashMap<String, Stream>();
		inactiveStreamsStore = new HashMap<String, Stream>();
		for (String key : streamsStore.keySet()) {
			Stream tmp = streamsStore.get(key);
			if (tmp.isActive()) {
				activeStreamsStore.put(key, tmp);
			}
			else {
				inactiveStreamsStore.put(key, tmp);
			}

		}
		logger.debug("---  activeStreamsStore & inactiveStreamsStore update finished  ---");

	}
}
