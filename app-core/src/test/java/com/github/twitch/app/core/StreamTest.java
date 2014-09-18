package com.github.twitch.app.core;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StreamData.class)
public class StreamTest {

	private Map<String, Object> updatedActiveStreamMap;
	private Map<String, Object> updatedUnactiveStreamMap;
	private Stream oldStream;
	private Stream updatedActiveStream;
	private Stream updatedUnactiveStream;

	@Before
	public void init() {
		oldStream = new Stream("http://www.twitch.tv/followjotm", false, "followjotm", null, null, 0);
		updatedActiveStream = new Stream("http://www.twitch.tv/followjotm", true, "followjotm", "JotM",
				"Dota 2", 4375);
		updatedUnactiveStream = new Stream("http://www.twitch.tv/followjotm", false, null, null, null, 0);

		//init updatedActiveStreamMap
		updatedActiveStreamMap = new HashMap<String, Object>();
		Map<String, Object> streamActiveInfo = new HashMap<String, Object>();
		Map<String, Object> channelActiveInfo = new HashMap<String, Object>();
		streamActiveInfo.put("viewers", (long) 4375);
		streamActiveInfo.put("game", "Dota 2");
		channelActiveInfo.put("name", "followjotm");
		channelActiveInfo.put("status", "JotM");
		streamActiveInfo.put("channel", channelActiveInfo);
		updatedActiveStreamMap.put("stream", streamActiveInfo);

		//init updatedUnactiveStreamMap
		updatedUnactiveStreamMap = new HashMap<String, Object>();
		updatedUnactiveStreamMap.put("stream", null);

	}

	@Test
	public void updateActiveStreamTest() {
		PowerMockito.mockStatic(StreamData.class);
		Mockito.when(StreamData.getStreamData(oldStream.getUrl())).thenReturn(updatedActiveStreamMap);
		Stream actual = oldStream.update();
		PowerMockito.verifyStatic();
		StreamData.getStreamData(oldStream.getUrl());
		Assert.assertEquals(updatedActiveStream, actual);

	}

	@Test
	public void updateUnactiveStreamTest() {
		PowerMockito.mockStatic(StreamData.class);
		Mockito.when(StreamData.getStreamData(oldStream.getUrl())).thenReturn(updatedUnactiveStreamMap);
		Stream actual = oldStream.update();
		PowerMockito.verifyStatic();
		StreamData.getStreamData(oldStream.getUrl());
		Assert.assertEquals(updatedUnactiveStream, actual);
	}

}
