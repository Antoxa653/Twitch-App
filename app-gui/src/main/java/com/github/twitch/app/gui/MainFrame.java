package com.github.twitch.app.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch.app.common.AppPropertiesConstants;
import com.github.twitch.app.common.AppPropertiesLoader;
import com.github.twitch.app.core.CoreController;
import com.github.twitch.app.core.Stream;
import com.github.twitch.app.gui.AddNewStreamFrame.OkButtonEvent;
import com.github.twitch.app.gui.GuiConstants.StatusType;
import com.github.twitch.app.gui.GuiConstants.StreamPanelShow;
import com.github.twitch.app.gui.GuiConstants.StreamPanelSize;

class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(MainFrame.class);

	private StreamPanelSize currentSize;
	private StreamPanelShow currentShow;
	private int screenWidth;
	private int screenHeight;
	private int defaulFrametWidth;
	private int defaultFrameHeight;

	//private JFrame frame;
	private JMenuBar menuBar;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel statusPanel;
	private JScrollPane streamsScrollPane;
	private JProgressBar statusProgressBar;

	private DeleteButtonAction deleteAction;
	private StartButtonAction startAction;

	public MainFrame() {
		super();
		currentSize = StreamPanelSize.valueOf(AppPropertiesLoader.getInstance().getAppProperties()
				.getValue(AppPropertiesConstants.STREAM_PANEL_SIZE));
		currentShow = StreamPanelShow.valueOf(AppPropertiesLoader.getInstance().getAppProperties()
				.getValue(AppPropertiesConstants.STREAM_PANEL_SHOW));

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		screenWidth = toolkit.getScreenSize().width;
		screenHeight = toolkit.getScreenSize().height;
		defaulFrametWidth = screenWidth / 2;
		defaultFrameHeight = screenHeight / 2;

		//frame = new JFrame();
		this.setTitle("TSV");
		this.setSize(new Dimension(defaulFrametWidth, defaultFrameHeight));
		this.setMinimumSize(new Dimension(defaulFrametWidth, defaultFrameHeight));
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		menuBar = createMenuBar();
		this.setJMenuBar(menuBar);
		this.addWindowListener(new MainFrameWindow());

		Container main = this.getContentPane();
		GroupLayout layout = new GroupLayout(main);
		layout.setAutoCreateGaps(true);
		main.setLayout(layout);

		topPanel = createTopPanel();
		bottomPanel = createBottomPanel();
		statusPanel = createStatusPanel();

		statusProgressBar = createProgressBar();

		topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
		bottomPanel.add(BorderLayout.CENTER, createButtonsPanel());

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(topPanel)
				.addComponent(bottomPanel).addComponent(statusPanel));

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(topPanel)
				.addComponent(bottomPanel, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(statusPanel, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		this.pack();
		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
		this.setVisible(true);

		deleteAction = new DeleteButtonAction();
		startAction = new StartButtonAction();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, viewMenu, helpMenu;
		fileMenu = new JMenu("File");
		viewMenu = new JMenu("View");
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);

		JMenuItem openItem = new JMenuItem("Open");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppPropertiesLoader.getInstance().getAppProperties()
						.saveProperties(AppPropertiesConstants.PROPERTIES_FILE);
				System.exit(0);
			}
		});
		fileMenu.add(openItem);
		fileMenu.add(exitItem);

		JMenuItem showMenuItem = new JMenuItem("Streams:");
		viewMenu.add(showMenuItem);

		JRadioButtonMenuItem showAllMenuItem = new JRadioButtonMenuItem("Show All");
		showAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentShow = StreamPanelShow.ALL;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SHOW, StreamPanelShow.ALL.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		JRadioButtonMenuItem showOnlineMenuItem = new JRadioButtonMenuItem("Show Online");
		showOnlineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentShow = StreamPanelShow.ACTIVE;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SHOW, StreamPanelShow.ACTIVE.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		JRadioButtonMenuItem showOfflineMenuItem = new JRadioButtonMenuItem("Show Offline");
		showOfflineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentShow = StreamPanelShow.UNACTIVE;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SHOW, StreamPanelShow.UNACTIVE.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		ButtonGroup viewShowGroup = new ButtonGroup();
		viewShowGroup.add(showAllMenuItem);
		viewShowGroup.add(showOnlineMenuItem);
		viewShowGroup.add(showOfflineMenuItem);

		viewMenu.add(showAllMenuItem);
		viewMenu.add(showOnlineMenuItem);
		viewMenu.add(showOfflineMenuItem);

		switch (StreamPanelShow.valueOf(AppPropertiesLoader.getInstance().getAppProperties()
				.getValue(AppPropertiesConstants.STREAM_PANEL_SHOW))) {
		case ALL:
			showAllMenuItem.setSelected(true);
			break;
		case ACTIVE:
			showOnlineMenuItem.setSelected(true);
			break;
		case UNACTIVE:
			showOfflineMenuItem.setSelected(true);
			break;
		default:
			break;
		}

		viewMenu.add(new JSeparator(SwingConstants.HORIZONTAL));

		JMenuItem sizeMenuItem = new JMenuItem("Stream panel:");
		viewMenu.add(sizeMenuItem);

		JRadioButtonMenuItem smallSizeMenuItem = new JRadioButtonMenuItem("Small Size");
		smallSizeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSize = StreamPanelSize.SMALL;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SIZE, StreamPanelSize.SMALL.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		JRadioButtonMenuItem mediumSizeMenuItem = new JRadioButtonMenuItem("Medium Size");
		mediumSizeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSize = StreamPanelSize.MEDIUM;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SIZE, StreamPanelSize.MEDIUM.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		JRadioButtonMenuItem largeSizeMenuItem = new JRadioButtonMenuItem("Large Size");
		largeSizeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSize = StreamPanelSize.LARGE;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SIZE, StreamPanelSize.LARGE.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		ButtonGroup viewSizeGroup = new ButtonGroup();
		viewSizeGroup.add(smallSizeMenuItem);
		viewSizeGroup.add(mediumSizeMenuItem);
		viewSizeGroup.add(largeSizeMenuItem);

		viewMenu.add(smallSizeMenuItem);
		viewMenu.add(mediumSizeMenuItem);
		viewMenu.add(largeSizeMenuItem);

		switch (StreamPanelSize.valueOf(AppPropertiesLoader.getInstance().getAppProperties()
				.getValue(AppPropertiesConstants.STREAM_PANEL_SIZE))) {
		case SMALL:
			smallSizeMenuItem.setSelected(true);
			break;
		case MEDIUM:
			mediumSizeMenuItem.setSelected(true);
			break;
		case LARGE:
			largeSizeMenuItem.setSelected(true);
			break;
		default:
			break;

		}

		JMenuItem aboutItem = new JMenuItem("About");
		helpMenu.add(aboutItem);

		return menuBar;
	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(this.getWidth(), 88 * this.getHeight() / 100));
		topPanel.setLayout(new BorderLayout());
		return topPanel;
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(this.getWidth(), 7 * this.getHeight() / 100));
		bottomPanel.setLayout(new BorderLayout());
		return bottomPanel;
	}

	private JPanel createStatusPanel() {
		JPanel statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 5 * this.getHeight() / 100));
		statusPanel.setLayout(new BorderLayout());
		return statusPanel;
	}

	private JScrollPane createStreamsPanel(StreamPanelSize streamPanelSize, StreamPanelShow streamPanelShow) {
		JPanel streamsPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		streamsPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;

		switch (streamPanelShow) {
		case ALL:

			Map<String, Stream> streamsStoreMap = CoreController.getStreamStore(streamPanelShow.toString());
			for (String s : streamsStoreMap.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(createStreamPanel(streamPanelSize, streamsStoreMap.get(s)), c);
				gridy++;
			}
			break;
		case ACTIVE:

			Map<String, Stream> currentActiveStreamList = CoreController.getStreamStore(streamPanelShow.toString());
			for (String s : currentActiveStreamList.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(createStreamPanel(streamPanelSize, currentActiveStreamList.get(s)), c);
				gridy++;
			}
			break;
		case UNACTIVE:

			Map<String, Stream> currentUnactiveStreamList = CoreController.getStreamStore(streamPanelShow.toString());
			for (String s : currentUnactiveStreamList.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(createStreamPanel(streamPanelSize, currentUnactiveStreamList.get(s)), c);
				gridy++;
			}
			break;
		default:
			break;
		}
		// This block prevent stretching of streamsPanel, DONT DELETE!!!!!!!
		JLabel prevemtStrechingLabel = new JLabel();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1000;
		c.weighty = 1000;
		c.gridx = 0;
		c.gridy = gridy;
		streamsPanel.add(prevemtStrechingLabel, c);
		//

		streamsScrollPane = new JScrollPane(streamsPanel);
		streamsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		streamsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		streamsScrollPane.getVerticalScrollBar()
				.setPreferredSize(new Dimension(15, topPanel.getPreferredSize().height));

		streamsScrollPane.setBorder(BorderFactory.createEtchedBorder());
		return streamsScrollPane;
	}

	private JPanel createStreamPanel(StreamPanelSize streamPanelSize, final Stream stream) {
		JPanel streamPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		streamPanel.setLayout(layout);

		String[] items = { "mobile", "low", "medium", "high", "source" };
		final JComboBox<String> qualityBox = new JComboBox<String>(items);
		qualityBox.setPreferredSize(new Dimension(90, streamPanelSize.size));
		qualityBox.setSelectedIndex(4);

		final JLabel nameLabel = new JLabel(stream.getName());
		nameLabel.setPreferredSize(new Dimension(100, streamPanelSize.size));

		JLabel statusLabel = new JLabel(stream.getStatus());
		statusLabel.setPreferredSize(new Dimension(100, streamPanelSize.size));

		JLabel gameLabel = new JLabel(stream.getCurrentGame());
		gameLabel.setPreferredSize(new Dimension(50, streamPanelSize.size));

		JLabel viewersLabel = new JLabel();
		viewersLabel.setPreferredSize(new Dimension(50, streamPanelSize.size));
		if (stream.getViewers() == 0) {
			viewersLabel.setText("");

		} else {
			viewersLabel.setText(String.valueOf(stream.getViewers()));
		}

		JButton startButton = createCustomStartButton(streamPanelSize, stream.isActive());
		if (stream.isActive()) {
			startButton.addActionListener(startAction);
		}

		JButton deleteButton = createCustomDeleteButton(streamPanelSize);
		deleteButton.addActionListener(deleteAction);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		streamPanel.add(startButton, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 10, 0, 10);
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		streamPanel.add(nameLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 10;
		c.weighty = 10;
		c.gridx = 2;
		c.gridy = 0;
		streamPanel.add(statusLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 3;
		c.gridy = 0;
		streamPanel.add(gameLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 4;
		c.gridy = 0;
		streamPanel.add(viewersLabel, c);
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 5;
		c.gridy = 0;
		streamPanel.add(qualityBox, c);
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 6;
		c.gridy = 0;
		streamPanel.add(deleteButton, c);

		return streamPanel;

	}

	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		buttonsPanel.setLayout(layout);

		JButton updateButton = new JButton("Update");
		updateButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		updateButton.putClientProperty("JButton.segmentPosition", "first");
		updateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel buttonsPanel = (JPanel) bottomPanel.getComponent(0);
				buttonsPanel.getComponent(0).setEnabled(false);
				buttonsPanel.getComponent(1).setEnabled(false);
				buttonsPanel.getComponent(2).setEnabled(false);
				buttonsPanel.getComponent(3).setEnabled(false);

				statusPanel.removeAll();
				statusPanel.add(BorderLayout.CENTER, statusProgressBar);
				statusPanel.revalidate();
				statusPanel.repaint();

				StreamUpdater streamUpdater = new StreamUpdater();
				streamUpdater.execute();

			}
		});

		JButton saveButton = new JButton("Save");
		saveButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		saveButton.putClientProperty("JButton.segmentPosition", "middle");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StreamSaver streamSaver = new StreamSaver();
				streamSaver.execute();
			}
		});

		JButton addButton = new JButton("Add");
		addButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		addButton.putClientProperty("JButton.segmentPosition", "middle");
		//
		final JFrame frame = this;
		final JPanel panel = topPanel;
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				AddNewStreamFrame s = new AddNewStreamFrame(frame);				

			}
		});

		JButton prefButton = new JButton("Preferences");
		prefButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		prefButton.putClientProperty("JButton.segmentPosition", "last");

		prefButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PropertiesFrame(frame);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		buttonsPanel.add(updateButton, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		buttonsPanel.add(saveButton, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 2;
		c.gridy = 0;
		buttonsPanel.add(addButton, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 3;
		c.gridy = 0;
		buttonsPanel.add(prefButton, c);

		return buttonsPanel;
	}

	private JProgressBar createProgressBar() {
		JProgressBar progressBar = new JProgressBar();
		progressBar.setLayout(new BorderLayout());
		progressBar.setIndeterminate(true);

		JLabel infoLabel = new JLabel();
		infoLabel.setFont(new Font("Verdana", Font.CENTER_BASELINE, 10));
		infoLabel.setText(StatusType.WAIT.getStatus());
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setVerticalAlignment(JLabel.CENTER);

		progressBar.add(BorderLayout.CENTER, infoLabel);
		return progressBar;
	}

	private void createStatusPanelInfoLabel(StatusType statusType) {
		JLabel statusLabel = new JLabel();
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		statusLabel.setVerticalAlignment(JLabel.CENTER);

		statusPanel.removeAll();
		statusPanel.add(BorderLayout.CENTER, statusLabel);
		statusLabel.setText(statusType.getStatus());
		statusPanel.revalidate();
		statusPanel.repaint();
	}

	private JButton createCustomStartButton(StreamPanelSize streamPanelSize, boolean streamActivityStatus) {
		JButton startButton = new JButton();
		startButton.setFocusPainted(false);
		startButton.setRolloverEnabled(true);
		startButton.setBorderPainted(false);
		startButton.setContentAreaFilled(false);
		startButton.setPreferredSize(new Dimension(streamPanelSize.size, streamPanelSize.size));
		if (streamActivityStatus) {
			switch (streamPanelSize) {
			case SMALL:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_ACTIVE_16_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass()
						.getResource(GuiConstants.START_ACTIVE_18_ICON)));
				break;
			case MEDIUM:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_ACTIVE_32_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass()
						.getResource(GuiConstants.START_ACTIVE_34_ICON)));
				break;
			case LARGE:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_ACTIVE_64_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass()
						.getResource(GuiConstants.START_ACTIVE_66_ICON)));
				break;
			default:
				break;
			}
		} else {
			switch (streamPanelSize) {
			case SMALL:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_UNACTIVE_16_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(
						GuiConstants.START_UNACTIVE_18_ICON)));
				break;
			case MEDIUM:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_UNACTIVE_32_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(
						GuiConstants.START_UNACTIVE_34_ICON)));
				break;
			case LARGE:
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_UNACTIVE_64_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(
						GuiConstants.START_UNACTIVE_66_ICON)));
				break;
			default:
				break;
			}
		}
		return startButton;
	}

	private JButton createCustomDeleteButton(StreamPanelSize streamPanelSize) {
		JButton deleteButton = new JButton();
		deleteButton.setFocusPainted(false);
		deleteButton.setRolloverEnabled(true);

		deleteButton.setBorderPainted(false);
		deleteButton.setContentAreaFilled(false);
		deleteButton.setPreferredSize(new Dimension(streamPanelSize.size, streamPanelSize.size));
		switch (streamPanelSize) {
		case SMALL:
			deleteButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_16_ICON)));
			deleteButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_18_ICON)));
			break;
		case MEDIUM:
			deleteButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_32_ICON)));
			deleteButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_34_ICON)));
			break;
		case LARGE:
			deleteButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_64_ICON)));
			deleteButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(GuiConstants.DELETE_66_ICON)));
			break;
		default:
			break;
		}
		return deleteButton;
	}

	private int createDeleteOptionPane() {
		return JOptionPane.showConfirmDialog(this, "Do you whant to delete channel?", "Delete stream",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	public void createAddOptionPane() {

	}

	private class StreamUpdater extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() {
			CoreController.updateStreamStore();
			return null;
		}

		@Override
		protected void done() {
			createStatusPanelInfoLabel(StatusType.UPDATE);

			Timer removeStatusTimer = new Timer(0, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					statusPanel.removeAll();
					statusPanel.revalidate();
					statusPanel.repaint();
				}

			});
			removeStatusTimer.setInitialDelay(5000);
			removeStatusTimer.setRepeats(false);
			removeStatusTimer.start();

			JPanel buttonsPanel = (JPanel) bottomPanel.getComponent(0);
			buttonsPanel.getComponent(0).setEnabled(true);
			buttonsPanel.getComponent(1).setEnabled(true);
			buttonsPanel.getComponent(2).setEnabled(true);
			buttonsPanel.getComponent(3).setEnabled(true);

			topPanel.removeAll();
			topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
			topPanel.revalidate();
			topPanel.repaint();
		}
	}

	private class StreamSaver extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			CoreController.saveStreamStore();
			return null;
		}

		protected void done() {
			createStatusPanelInfoLabel(StatusType.SAVE);

			Timer removeStatusTimer = new Timer(0, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					statusPanel.removeAll();
					statusPanel.revalidate();
					statusPanel.repaint();
				}

			});
			removeStatusTimer.setInitialDelay(5000);
			removeStatusTimer.setRepeats(false);
			removeStatusTimer.start();
		}
	}

	private class DeleteButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			JPanel parentPanel = (JPanel) button.getParent();
			final JLabel nameLabel = (JLabel) parentPanel.getComponent(1);
			if (createDeleteOptionPane() == 0) {
				CoreController.removeStream(nameLabel.getText());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, createStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}

		}

	}

	private class StartButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			JPanel parentPanel = (JPanel) button.getParent();
			JLabel nameLabel = (JLabel) parentPanel.getComponent(1);
			final JComboBox<String> qialityBox = (JComboBox<String>) parentPanel.getComponent(5);
			final Stream stream = CoreController.getStream(nameLabel.getText());

			CoreController.startStream(stream, (String) qialityBox.getSelectedItem());

		}
	}

	private class MainFrameWindow extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent we) {
			AppPropertiesLoader.getInstance().getAppProperties()
					.saveProperties(AppPropertiesConstants.PROPERTIES_FILE);
			System.exit(0);
		}

	}

}
