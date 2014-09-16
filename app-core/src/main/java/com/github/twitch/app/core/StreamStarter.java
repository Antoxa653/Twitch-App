package com.github.twitch.app.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch.app.common.AppPropertiesConstants;
import com.github.twitch.app.common.AppPropertiesLoader;

final class StreamStarter {
	private static Logger logger = LoggerFactory.getLogger(StreamStarter.class);

	private StreamStarter() {

	}

	static void start(Stream stream, String quality) {
		File streamFile = createStreamFile(stream, quality);
		try {
			Runtime.getRuntime().exec(
					"cmd /c start " + streamFile.toString());
		} catch (IOException e) {
			logger.error("Error while opening stream file:" + streamFile, e);
		}
	}

	private static File createStreamFile(Stream stream, String quality) {
		String streamFilePath = new File(AppPropertiesConstants.PROPERTIES_FILE_PATH).getParentFile().toString();
		File streamFile = new File(streamFilePath + File.separator + stream.getName() + ".bat");
		StringBuilder sb = new StringBuilder();
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(streamFile))) {
			sb.append("@echo ");
			sb.append(System.getProperty("line.separator"));
			sb.append("cd /D");
			sb.append(AppPropertiesLoader.getInstance().getAppProperties()
					.getValue(AppPropertiesConstants.LIVESTREAMER_PATH));
			sb.append(System.getProperty("line.separator"));
			sb.append("livestreamer.exe");
			sb.append(" ");
			sb.append(stream.getUrl());
			sb.append(" ");
			sb.append(quality);
			sb.append(System.getProperty("line.separator"));
			sb.append("@echo off");
			pw.println(sb.toString());
			sb.setLength(0);
		} catch (FileNotFoundException e) {
			logger.error("Stream file was not found in: " + streamFile, e);
		}
		return streamFile;
	}

}
