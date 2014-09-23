package com.github.twitch.app.loader;

import java.awt.EventQueue;
import com.github.twitch.app.common.AppPropertiesLoader;
import com.github.twitch.app.gui.LookAndFeel;
import com.github.twitch.app.gui.MainFrame;

public final class Loader {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
		
		
		LookAndFeel iStyle = new LookAndFeel();
		iStyle.init();
		
		AppPropertiesLoader.getInstance();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame();
			}
		});

	}
}
