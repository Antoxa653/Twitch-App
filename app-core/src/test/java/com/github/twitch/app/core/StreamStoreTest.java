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

		StreamStore.init(streamStoreTestFile);
		StreamStore.addStream("Dendi", testStream1);
		StreamStore.addStream("Puppey", testStream2);
		StreamStore.addStream("Xboct", testStream3);
		StreamStore.addStream("Kuroky", testStream4);
		StreamStore.addStream("Funnik", testStream5);
	}

	@After
	public void clearInvirionment() {
		StreamStore.removeAll();
	}

	@Test
	public void saveInitTest() {
		StreamStore.save(streamStoreTestFile);
		StreamStore.removeAll();
		StreamStore.init(streamStoreTestFile);
		Assert.assertEquals(testStream1, StreamStore.getStream("Dendi"));
		Assert.assertEquals(testStream2, StreamStore.getStream("Puppey"));
		Assert.assertEquals(testStream3, StreamStore.getStream("Xboct"));
		Assert.assertEquals(testStream4, StreamStore.getStream("Kuroky"));
		Assert.assertEquals(testStream5, StreamStore.getStream("Funnik"));
	}
}
