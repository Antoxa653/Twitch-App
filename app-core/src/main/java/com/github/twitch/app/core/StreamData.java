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

final class StreamData {
	private static Logger logger = LoggerFactory.getLogger(StreamData.class);

	private StreamData() {
	}

	static synchronized Map<String, Object> getStreamData(String streamUrl) {
		Map<String, Object> updatedStreamInfo = null;
		if (streamUrl != null) {
			String result = null;
			URL request = null;
			HttpURLConnection connection = null;
			StringBuffer response = null;
			try {
				request = createRequestAddress(streamUrl);
				connection = (HttpURLConnection) request.openConnection();
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				int responseCode = connection.getResponseCode();

				switch (responseCode) {
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

					updatedStreamInfo = responseToMap(result);
					break;
				case 400:
					throw new ConnectException("Request URL does not exist:" + request);
				case 404:
					throw new ConnectException("Twitch server did not respond to a request");
				default:
					throw new ConnectException("Unknown response code from Twitch server");
				}

			} catch (ProtocolException e) {
				logger.error("Can't open HttpURLConnection to " + request, e);
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
	private static synchronized Map<String, Object> responseToMap(String response) throws ParseException {
		Map<String, Object> streamInfoMap = null;
		if (response != null) {
			streamInfoMap = (Map<String, Object>) new JSONParser().parse(response);
		}
		return streamInfoMap;
	}

	private static synchronized URL createRequestAddress(String url) throws MalformedURLException {
		String endpoint = "https://api.twitch.tv/kraken/streams";
		return new URL(endpoint + new URL(url).getFile());
	}

}
