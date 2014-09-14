package com.github.twitch.app.core;

import java.util.Collection;
import java.util.Map;

public final class CoreControler {

	private CoreControler() {

	}

	public static void update() {
		Map<String, Stream> streamStore = StreamStore.getStore();
		Collection<Stream> streamStoreValues = streamStore.values();
		for (Stream stream : streamStoreValues) {
			new Thread(new Update(stream)).start();
		}

	}

}

class Update implements Runnable {
	private Stream stream;

	public Update(Stream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		stream.update();
	}
}
