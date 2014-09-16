package com.github.twitch.app.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.github.twitch.app.common.AppPropertiesConstants;
import com.github.twitch.app.common.AppPropertiesLoader;

public class PropertiesFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int screenWidth;
	private int screenHeight;
	private JLabel lsPathLabel;
	private final JLabel errorLabel;
	private final JTextField lsPathTextField;
	private JButton applyButton;
	private JButton closeButton;

	public PropertiesFrame() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		screenWidth = toolkit.getScreenSize().width / 4;
		screenHeight = toolkit.getScreenSize().height / 4;

		this.setTitle("Properties");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setSize(new Dimension(screenWidth, screenHeight));

		Container main = this.getContentPane();
		//GroupLayout layout = new GroupLayout(main);
		//layout.setAutoCreateContainerGaps(true);
		//layout.setAutoCreateGaps(true);
		//main.setLayout(layout);
		GridBagLayout layout = new GridBagLayout();
		main.setLayout(layout);

		lsPathLabel = new JLabel("Livestreamer Path:");
		errorLabel = new JLabel();
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setForeground(Color.RED);
		lsPathTextField = new JTextField(AppPropertiesLoader.getInstance().getAppProperties()
				.getValue(AppPropertiesConstants.LIVESTREAMER_PATH));
		applyButton = new JButton("Apply");
		applyButton.addMouseListener(new ApplyButtonEvent(this));
		closeButton = new JButton("Close");
		closeButton.addMouseListener(new CloseButtonEvent(this));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		main.add(lsPathLabel, c);
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(10, 0, 10, 10);
		c.weightx = 100;
		c.weighty = 1;
		c.gridwidth = 7;
		c.gridheight = 1;
		c.gridx = 3;
		c.gridy = 0;
		main.add(lsPathTextField, c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 0, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 100;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 8;
		c.gridy = 8;
		main.add(applyButton, c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 100;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 9;
		c.gridy = 8;
		main.add(closeButton, c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 100;
		c.weighty = 100;
		c.gridwidth = 8;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 8;
		main.add(new JLabel(), c);
		c.anchor = GridBagConstraints.PAGE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 10;
		c.gridx = 0;
		c.gridy = 9;
		main.add(errorLabel, c);

		/*
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup().addComponent(lsPathLabel).addComponent(lsPathTextField))
				.addGroup(layout.createSequentialGroup().addComponent(applyButton).addComponent(closeButton))
				.addComponent(errorLabel));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lsPathLabel)
								.addComponent(lsPathTextField))
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(applyButton)
								.addComponent(closeButton)).addComponent(errorLabel));

		 */
		this.setVisible(true);
	}

	class ApplyButtonEvent extends MouseAdapter {
		private JFrame frame;

		ApplyButtonEvent(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			String newLiveStreamerPath = lsPathTextField.getText();
			String oldLiveStreamerPath = AppPropertiesLoader.getInstance().getAppProperties().getValue(AppPropertiesConstants.LIVESTREAMER_PATH); 
			if (newLiveStreamerPath.equals(oldLiveStreamerPath)) {
				frame.setVisible(false);
				frame.dispose();
				return;
			}
			if (!newLiveStreamerPath.equals(oldLiveStreamerPath) && AppPropertiesLoader.getInstance().getAppProperties().checkLivestreamerPath(newLiveStreamerPath)) {
				AppPropertiesLoader.getInstance().getAppProperties().setValueAndSaveProperties(AppPropertiesConstants.PROPERTIES_FILE, AppPropertiesConstants.LIVESTREAMER_PATH, newLiveStreamerPath);
				frame.setVisible(false);
				frame.dispose();
				return;
			}
			else {
				errorLabel.setText("Wrong livestreamer path");
				frame.repaint();
				frame.revalidate();
			}

		}
	}

	class CloseButtonEvent extends MouseAdapter {
		private JFrame frame;

		CloseButtonEvent(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			frame.setVisible(false);
			frame.dispose();

		}
	}

}
