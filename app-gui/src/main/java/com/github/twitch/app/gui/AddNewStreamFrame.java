package com.github.twitch.app.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.twitch.app.core.CoreController;
import com.github.twitch.app.core.Stream;

public class AddNewStreamFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int screenWidth;
	private int screenHeight;
	private JButton okButton;
	private JButton cancelButton;
	private final JTextField streamNameTextField;
	private final JTextField streamUrlTextField;
	private JLabel nameLabel;
	private JLabel urlLabel;
	private JLabel errorLabel;

	AddNewStreamFrame() {
		super();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		screenWidth = toolkit.getScreenSize().width / 4;
		screenHeight = toolkit.getScreenSize().height / 4;

		this.setTitle("Add new stream");
		this.setSize(new Dimension(screenWidth, screenHeight));
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		Container main = this.getContentPane();
		GroupLayout layout = new GroupLayout(main);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		main.setLayout(layout);

		nameLabel = new JLabel("Enter name:");
		urlLabel = new JLabel("Enter URL:");
		errorLabel = new JLabel();
		okButton = new JButton("Ok");
		okButton.addMouseListener(new OkButtonEvent(this));
		cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new CancelButtonEvent(this));
		streamNameTextField = new JTextField();
		streamNameTextField.setMaximumSize(new Dimension(screenWidth - 25, 25));
		streamUrlTextField = new JTextField();
		streamUrlTextField.setMaximumSize(new Dimension(screenWidth - 25, 25));

		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(nameLabel)
				.addComponent(streamNameTextField)
				.addComponent(urlLabel)
				.addComponent(streamUrlTextField)
				.addGroup(
						layout.createSequentialGroup().addComponent(okButton)
								.addComponent(cancelButton))
				.addComponent(errorLabel));

		layout.setVerticalGroup(layout
				.createSequentialGroup().addComponent(nameLabel)
				.addComponent(streamNameTextField)
				.addComponent(urlLabel)
				.addComponent(streamUrlTextField)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(okButton)
								.addComponent(cancelButton))
				.addComponent(errorLabel));
		this.setVisible(true);

	}

	class OkButtonEvent extends MouseAdapter {
		private JFrame frame;

		OkButtonEvent(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (streamNameTextField.getText().isEmpty()) {
				errorLabel.setText("Please enter the stream name");
				errorLabel.setForeground(Color.RED);
				frame.repaint();
				frame.revalidate();
				return;
			}
			if (streamUrlTextField.getText().isEmpty()) {
				errorLabel.setText("Please enter the stream URL");
				errorLabel.setForeground(Color.RED);
				frame.repaint();
				frame.revalidate();
				return;
			}
			if (!CoreController.checkUrl(streamUrlTextField.getText())) {
				errorLabel.setText("Invalid URL");
				errorLabel.setForeground(Color.RED);
				frame.repaint();
				frame.revalidate();
				return;
			}
			
			else {
				Stream newStream = new Stream(streamUrlTextField.getText(), false,
						streamNameTextField.getText(), null, null, 0);
				CoreController.addStream(streamNameTextField.getText(), newStream);
				newStream.update();
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}

	class CancelButtonEvent extends MouseAdapter {
		private JFrame frame;

		CancelButtonEvent(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			frame.setVisible(false);
			frame.dispose();

		}
	}
}
