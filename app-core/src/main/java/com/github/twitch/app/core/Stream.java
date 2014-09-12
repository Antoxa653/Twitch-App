package com.github.twitch.app.core;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stream implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(Stream.class);
	private URL url;
	private boolean state;
	private String name;
	private String status;
	private String currentGame;
	private int viewers;

	public Stream(String url, boolean state, String name, String status, String currentGame, int viewers) {
		super();
		this.name = name;
		this.state = state;
		this.status = status;
		this.currentGame = currentGame;
		this.viewers = viewers;
		this.url = stringToURL(url);

	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(String currentGame) {
		this.currentGame = currentGame;
	}

	public int getViewers() {
		return viewers;
	}

	public void setViewers(int viewers) {
		this.viewers = viewers;
	}

	@Override
	public String toString() {
		return "Stream [url=" + url + ", state=" + state + ", name=" + name + ", status=" + status + ", currentGame="
				+ currentGame + ", viewers=" + viewers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentGame == null) ? 0 : currentGame.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (state ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + viewers;
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
		if (state != other.state)
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

	private URL stringToURL(String urlString) {
		URL url = null;
		if (urlString != null) {
			try {
				url = new URL(urlString);
			} catch (MalformedURLException e) {
				logger.error("Incorrect URL: " + urlString, e);
			}
		}
		return url;
	}

}
