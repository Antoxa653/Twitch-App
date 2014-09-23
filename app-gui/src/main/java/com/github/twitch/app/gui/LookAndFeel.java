package com.github.twitch.app.gui;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookAndFeel {
	private Logger log = LoggerFactory.getLogger(LookAndFeel.class);	

	public void init() {
		try {
			for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());					
				}
			}
		} catch (ClassNotFoundException e) {
			log.error("Bad class name", e);
		} catch (InstantiationException e) {
			log.error("Cant initiate class instance of ", e);
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
		} catch (UnsupportedLookAndFeelException e) {
			log.error("L&F them cant been used on this system ", e);
		}
		changeDefaultAppFont();
		log.debug("Interface style Nimbus used");
	}

	private void changeDefaultAppFont() {
		FontUIResource f = new FontUIResource(new Font("Verdana", 0, 12));
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource orig = (FontUIResource) value;
				Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}
}