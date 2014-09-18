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

	static void start(String key, Stream stream, String quality) {
		logger.debug("--- Starting " + key + " stream ---");
		File streamFile = createStreamFile(stream, quality);
		try {
			Runtime.getRuntime().exec(
					"cmd /c start " + streamFile.toString());
			logger.debug("--- Stream " + key + "was started ---");
		} catch (IOException e) {
			logger.error("Error while opening stream file:" + streamFile, e);
		}
	}

	private static File createStreamFile(Stream stream, String quality) {
		StringBuilder sb = new StringBuilder();
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(CoreConstants.LIVESTREAMER_BAT_FILE))) {
			logger.debug("--- Creating stream file ---");
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
			logger.debug("--- Creatiion of stream file finished ---");
		} catch (FileNotFoundException e) {
			logger.error(CoreConstants.LIVESTREAMER_BAT_FILE.getName() + " file was not found in: "
					+ CoreConstants.LIVESTREAMER_BAT_PATH, e);
		}
		return CoreConstants.LIVESTREAMER_BAT_FILE;
	}

}
