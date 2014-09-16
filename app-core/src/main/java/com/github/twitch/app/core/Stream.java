package com.github.twitch.app.core;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stream implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(Stream.class);
	private String url = null;
	private boolean activity = false;
	private String name = null;
	private String status = null;
	private String currentGame = null;
	private long viewers = 0;

	public Stream(String url, boolean activity, String name, String status, String currentGame, int viewers) {
		super();
		this.name = name;
		this.activity = activity;
		this.status = status;
		this.currentGame = currentGame;
		this.viewers = viewers;
		this.url = url;
	}

	public Stream update() {
		Map<String, Object> updatedStream = StreamData.getStreamData(this.getUrl());

		if (updatedStream.get("stream") != null) {
			Map<String, Object> streamInfo = (Map<String, Object>) updatedStream.get("stream");
			Map<String, Object> channelInfo = (Map<String, Object>) streamInfo.get("channel");
			this.status = (String) channelInfo.get("status");			
			this.currentGame = (String) streamInfo.get("game");
			this.viewers = (long) streamInfo.get("viewers");
			this.activity = true;
		}
		else {
			this.status = null;
			this.currentGame = null;
			this.viewers = 0;
			this.activity = false;
		}
		return this;
	}

	public String getUrl() {
		return url;
	}

	public boolean isActive() {
		return activity;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getCurrentGame() {
		return currentGame;
	}

	public long getViewers() {
		return viewers;
	}

	@Override
	public String toString() {
		return "Stream [url=" + url + ", state=" + activity + ", name=" + name + ", status=" + status + ", currentGame="
				+ currentGame + ", viewers=" + viewers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentGame == null) ? 0 : currentGame.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (activity ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + (int) (viewers ^ (viewers >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stream other = (Stream) obj;
		if (currentGame == null) {
			if (other.currentGame != null)
				return false;
		} else if (!currentGame.equals(other.currentGame))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (activity != other.activity)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (viewers != other.viewers)
			return false;
		return true;
	}

}
