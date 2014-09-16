package com.github.twitch.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class PreferencesDialog {
	private int screenWidth;
	private int screenHeight;
	private int defaultDialogFrameWidth;
	private int defaultDialogFrameHeight;

	private JPanel preferencesBottomPanel;
	private JSplitPane preferencesTopPanel;
	private JDialog preferencesDialog;

	PreferencesDialog(Container parentContainer) {
	}
	/*
		this.preferences = preferences;

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		screenWidth = toolkit.getScreenSize().width;
		screenHeight = toolkit.getScreenSize().height;
		defaultDialogFrameWidth = screenWidth / 3;
		defaultDialogFrameHeight = screenHeight / 3;

		preferencesDialog = new JDialog();
		preferencesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		preferencesDialog.setSize(new Dimension(defaultDialogFrameWidth, defaultDialogFrameHeight));
		preferencesDialog.setMinimumSize(new Dimension(defaultDialogFrameWidth, defaultDialogFrameHeight));
		preferencesDialog.setLocationRelativeTo(parentContainer);
		preferencesDialog.setResizable(true);
		preferencesDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		preferencesDialog.setTitle("Preferences");

		Container main = preferencesDialog.getContentPane();
		GroupLayout layout = new GroupLayout(main);
		main.setLayout(layout);

		preferencesTopPanel = createPreferencesTopPanel();
		preferencesBottomPanel = createPropertiesBottomPanel();

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(preferencesTopPanel).addComponent(preferencesBottomPanel));

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(preferencesTopPanel)
				.addComponent(preferencesBottomPanel, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		preferencesDialog.pack();
		preferencesDialog.setMinimumSize(new Dimension(preferencesDialog.getWidth(), preferencesDialog.getHeight()));
		preferencesDialog.setVisible(true);
	}

	private JSplitPane createPreferencesTopPanel() {
		// Left part content		
		JPanel leftPanel = leftPanel();

		// Right part content
		JPanel rightPanel = rightPanel();

		JSplitPane splitPane = new JSplitPane(SwingConstants.VERTICAL, leftPanel, rightPanel);
		splitPane.setPreferredSize(new Dimension(preferencesDialog.getWidth(), 9 * preferencesDialog.getHeight() / 10));
		splitPane.setBackground(Color.WHITE);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(125);
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	private JPanel leftPanel() {
		JPanel panel = new JPanel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode general = new DefaultMutableTreeNode("General");
		root.add(general);

		final JTree tree = new JTree(root);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node == null) {
					return;
				}
				Object nodeInfo = node.getUserObject();
				if ("General".equals(nodeInfo)) {
					JPanel right = (JPanel) preferencesTopPanel.getRightComponent();

					right.add(BorderLayout.CENTER, generalNodePanel());
					right.revalidate();
					right.repaint();
				}
			}
		});

		panel.add(tree);

		return panel;
	}

	private JPanel rightPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		return panel;
	}

	private JPanel generalNodePanel() {
		JPanel panel = new JPanel();

		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panel.setLayout(layout);

		final JTextField pathField = new JTextField();
		JButton applyButton = new JButton("Apply");
		applyButton.setPreferredSize(new Dimension(100, 25));
		applyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (preferences.isLivestreamerExistInPath(pathField.getText())) {
					preferences.setLivestreamerPath(pathField.getText());
					preferences.savePreferences();
				}
			}
		});
		JLabel label = new JLabel("Livestreamer path");

		if (preferences.isLivestreamerExistInPath(((String) preferences.getLivestreamerPath()))) {
			pathField.setText((String) preferences.getLivestreamerPath());
			pathField.setEditable(false);
		}

		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addComponent(label).addComponent(pathField))
				.addGroup(
						layout.createSequentialGroup()
								.addGap(1, 1, Short.MAX_VALUE)
								.addComponent(applyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label)
								.addComponent(pathField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
				.addGap(1, 1, Short.MAX_VALUE)
				.addComponent(applyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE));

		return panel;
	}

	private JPanel createPropertiesBottomPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(preferencesDialog.getWidth(), preferencesDialog.getHeight() / 10));

		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);

		JButton okButton = new JButton("Ok");
		okButton.setPreferredSize(new Dimension(100, 25));
		okButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent me) {
				preferencesDialog.dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 25));
		cancelButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent me) {
				preferencesDialog.dispose();
			}
		});

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGap(1, 1, Short.MAX_VALUE)
				.addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE).addGap(15, 15, 15));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(okButton)
				.addComponent(cancelButton));

		panel.add(okButton);
		panel.add(cancelButton);
		return panel;
	}
	*/
}
