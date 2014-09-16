package com.github.twitch.app.gui;

import java.awt.EventQueue;

import com.github.twitch.app.common.AppPropertiesLoader;
import com.github.twitch.app.core.CoreController;

public final class Loader {

	public static void main(String[] args) {
		//init properties
		AppPropertiesLoader.getInstance();

		//init StreamStore
		CoreController.initStreamStore();

		//gui
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame();
			}

		});

	}

	private Loader() {

	}

}
