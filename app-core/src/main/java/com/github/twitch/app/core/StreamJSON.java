package com.github.twitch.app.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StreamJSON {
	private static Logger logger = LoggerFactory.getLogger(StreamJSON.class);

	private StreamJSON() {
	}

	public static Map<String, Object> getJSON(String streamUrl) {
		Map<String, Object> updatedStreamInfo = null;
		if (streamUrl != null) {
			String result = null;
			URL requestURL = null;
			HttpURLConnection connection = null;
			StringBuffer response = null;
			try {
				requestURL = createRequestURL(streamUrl);
				connection = (HttpURLConnection) requestURL.openConnection();
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				int responseCode = connection.getResponseCode();

				switch (responseCode) {
				case 400:
					throw new ConnectException("Request URL does not exist:" + requestURL);
				case 404:
					throw new ConnectException("Twitch server did not respond to a request");
				case 200:
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
					response = new StringBuffer();
					String line;
					while ((line = br.readLine()) != null) {
						response.append(line);
					}
					result = response.toString();
					response.setLength(0);
					br.close();

					updatedStreamInfo = toMap(result);
					break;
				default:
					throw new ConnectException("Unknown response code from Twitch server");
				}

			} catch (ProtocolException e) {
				logger.error("Can't open HttpURLConnection to " + requestURL, e);
			} catch (UnsupportedEncodingException e) {
				logger.error("Can't encode input stream from twitch server", e);
			} catch (MalformedURLException e) {
				logger.error("Error occurs while creating requestURL", e);
			} catch (ParseException e) {
				logger.error("ParseException occurs while parsing stream's JSON", e);
			} catch (ConnectException e) {
				logger.error("Incorect responseCode from Twitch server", e);
			} catch (IOException e) {
				logger.error("Exception when trying to get stream's JSON from Twitch server", e);
			}
		}
		return updatedStreamInfo;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> toMap(String json) throws ParseException {
		Map<String, Object> streamInfo = null;
		if (json != null) {
			streamInfo = (Map<String, Object>) new JSONParser().parse(json);
		}
		return streamInfo;
	}

	private static URL createRequestURL(String streamURL) throws MalformedURLException {
		String endpoint = "https://api.twitch.tv/kraken/streams";
		URL requestUrl = null;
		URL streamUrl = new URL(streamURL);
		String temp = streamUrl.getFile();
		requestUrl = new URL(endpoint + temp);
		return requestUrl;
	}

}
