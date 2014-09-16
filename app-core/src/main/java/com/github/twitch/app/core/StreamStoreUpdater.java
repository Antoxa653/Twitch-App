package com.github.twitch.app.core;

class StreamStoreUpdater implements Runnable {
	private Stream stream;
	StreamStoreUpdater(Stream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		stream.update();		
	}
}
