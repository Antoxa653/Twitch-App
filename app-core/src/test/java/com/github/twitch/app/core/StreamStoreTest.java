package com.github.twitch.app.core;

import java.io.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StreamStoreTest {
	private File streamStoreTestFile = new File("src/test/resources/streamStoreTest.tmp");
	private Stream testStream1;
	private Stream testStream2;
	private Stream testStream3;
	private Stream testStream4;
	private Stream testStream5;

	@Before
	public void prepareInvirionment() {
		testStream1 = new Stream("http:\\twitch.tv\\dendi", true, "Dendi", "Dendi play dota",
				"Dota2", 25000);
		testStream2 = new Stream("http:\\twitch.tv\\puppey", false, "Puppey", "Puppey play dota",
				"Dota2", 5000);
		testStream3 = new Stream("http:\\twitch.tv\\xboct", false, "Xboct", "Xboct play dota",
				"Vodka2", 7000);
		testStream4 = new Stream("http:\\twitch.tv\\kuroky", false, "Kuroky", "Kuroky play dota",
				"Dota2", 15000);
		testStream5 = new Stream("http:\\twitch.tv\\funnik", true, "Funnik", "Funnik play dota",
				"Dota2", 6000);

		StreamsStore.init(streamStoreTestFile);
		StreamsStore.addStream("Dendi", testStream1);
		StreamsStore.addStream("Puppey", testStream2);
		StreamsStore.addStream("Xboct", testStream3);
		StreamsStore.addStream("Kuroky", testStream4);
		StreamsStore.addStream("Funnik", testStream5);
	}

	@After
	public void clearInvirionment() {
		StreamsStore.removeAll();
		streamStoreTestFile.deleteOnExit();
	}

	@Test
	public void saveInitTest() {
		StreamsStore.save(streamStoreTestFile);
		StreamsStore.removeAll();
		StreamsStore.init(streamStoreTestFile);
		Assert.assertEquals(testStream1, StreamsStore.getStream("Dendi"));
		Assert.assertEquals(testStream2, StreamsStore.getStream("Puppey"));
		Assert.assertEquals(testStream3, StreamsStore.getStream("Xboct"));
		Assert.assertEquals(testStream4, StreamsStore.getStream("Kuroky"));
		Assert.assertEquals(testStream5, StreamsStore.getStream("Funnik"));
	}
}
