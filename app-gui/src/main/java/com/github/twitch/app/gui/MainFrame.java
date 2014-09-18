package com.github.twitch.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

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
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.github.twitch.app.common.AppPropertiesConstants;
import com.github.twitch.app.common.AppPropertiesLoader;
import com.github.twitch.app.core.CoreController;
import com.github.twitch.app.core.Stream;
import com.github.twitch.app.gui.GuiConstants.StatusType;
import com.github.twitch.app.gui.GuiConstants.StreamPanelShow;
import com.github.twitch.app.gui.GuiConstants.StreamPanelSize;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

	private StreamPanelSize currentSize;
	private StreamPanelShow currentShow;
	private int screenWidth;
	private int screenHeight;
	private int defaulFrametWidth;
	private int defaultFrameHeight;

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

		topPanel = createStreamsPanel();
		bottomPanel = createButtonsPanel();
		statusPanel = createStatusPanel();
		statusProgressBar = createProgressBar();

		bottomPanel.add(BorderLayout.CENTER, addButtonsPanel());

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

		StreamsPanelUpdater update = new StreamsPanelUpdater();
		update.execute();
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
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		});
		JRadioButtonMenuItem showOfflineMenuItem = new JRadioButtonMenuItem("Show Offline");
		showOfflineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentShow = StreamPanelShow.INACTIVE;
				AppPropertiesLoader.getInstance().getAppProperties()
						.setValue(AppPropertiesConstants.STREAM_PANEL_SHOW, StreamPanelShow.INACTIVE.toString());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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
		case INACTIVE:
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
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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

	private JPanel createStreamsPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(this.getWidth(), 88 * this.getHeight() / 100));
		topPanel.setLayout(new BorderLayout());
		return topPanel;
	}

	private JPanel createButtonsPanel() {
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

	private JScrollPane addStreamsPanel(StreamPanelSize streamPanelSize, StreamPanelShow streamPanelShow) {
		JPanel streamsPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		streamsPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;

		switch (streamPanelShow) {
		case ALL:

			Map<String, Stream> streamsStoreMap = CoreController.getStreamStore(streamPanelShow.toString());
			for (String stream : streamsStoreMap.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(addStreamPanel(streamPanelSize, stream, streamsStoreMap.get(stream)), c);
				gridy++;
			}
			break;
		case ACTIVE:

			Map<String, Stream> currentActiveStreamList = CoreController.getStreamStore(streamPanelShow.toString());
			for (String stream : currentActiveStreamList.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(addStreamPanel(streamPanelSize, stream, currentActiveStreamList.get(stream)), c);
				gridy++;
			}
			break;
		case INACTIVE:

			Map<String, Stream> currentUnactiveStreamList = CoreController.getStreamStore(streamPanelShow.toString());
			for (String stream : currentUnactiveStreamList.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(addStreamPanel(streamPanelSize, stream, currentUnactiveStreamList.get(stream)), c);
				gridy++;
			}
			break;
		default:
			Map<String, Stream> defaultStoreMap = CoreController.getStreamStore(streamPanelShow.toString());
			for (String stream : defaultStoreMap.keySet()) {
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = gridy;
				streamsPanel.add(addStreamPanel(streamPanelSize, stream, defaultStoreMap.get(stream)), c);
				gridy++;
			}
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

	private JPanel addStreamPanel(StreamPanelSize streamPanelSize, String streamName, final Stream stream) {
		JPanel streamPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		streamPanel.setLayout(layout);

		String[] items = { "mobile", "low", "medium", "high", "source", "audio" };
		final JComboBox<String> qualityBox = new JComboBox<String>(items);
		qualityBox.setPreferredSize(new Dimension(90, streamPanelSize.size));
		qualityBox.setSelectedIndex(4);

		final JLabel nameLabel = new JLabel(streamName);
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
			startButton.addMouseListener(startAction);
		}			

		JButton deleteButton = createCustomDeleteButton(streamPanelSize);
		deleteButton.addMouseListener(deleteAction);

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

	private JPanel addButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		buttonsPanel.setLayout(layout);

		JButton updateButton = new JButton("Update");
		updateButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		updateButton.putClientProperty("JButton.segmentPosition", "first");
		updateButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				StreamsPanelUpdater allStreamUpdater = new StreamsPanelUpdater();
				allStreamUpdater.execute();

			}
		});

		JButton saveButton = new JButton("Save");
		saveButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		saveButton.putClientProperty("JButton.segmentPosition", "middle");
		saveButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				StreamSaver streamSaver = new StreamSaver();
				streamSaver.execute();
			}
		});

		JButton addButton = new JButton("Add");
		addButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		addButton.putClientProperty("JButton.segmentPosition", "middle");
		//
		final JFrame frame = this;
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				new AddNewStreamFrame(frame);

			}
		});

		JButton propertiesButton = new JButton("Preferences");
		propertiesButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		propertiesButton.putClientProperty("JButton.segmentPosition", "last");

		propertiesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
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
		buttonsPanel.add(propertiesButton, c);

		return buttonsPanel;
	}

	private JProgressBar createProgressBar() {
		JProgressBar progressBar = new JProgressBar();
		progressBar.setLayout(new BorderLayout());
		progressBar.setIndeterminate(true);

		JLabel infoLabel = new JLabel();
		infoLabel.setFont(new Font("Verdana", Font.CENTER_BASELINE, 10));
		infoLabel.setText(StatusType.WAIT.message);
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
		statusLabel.setText(statusType.message);
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
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_ACTIVE_16_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass()
						.getResource(GuiConstants.START_ACTIVE_18_ICON)));
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
				startButton.setIcon(new ImageIcon(this.getClass().getResource(GuiConstants.START_UNACTIVE_16_ICON)));
				startButton.setRolloverIcon(new ImageIcon(this.getClass().getResource(
						GuiConstants.START_UNACTIVE_18_ICON)));
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

	private class AddNewStreamFrame extends JFrame {
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
		private JFrame mainFrame;

		AddNewStreamFrame(JFrame frame) {
			super();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			screenWidth = toolkit.getScreenSize().width / 4;
			screenHeight = toolkit.getScreenSize().height / 4;

			//
			this.mainFrame = frame;

			this.setTitle("Add new stream");
			this.setSize(new Dimension(screenWidth, screenHeight));
			this.setResizable(false);
			this.setLocationRelativeTo(mainFrame);

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
							null, null, null, 0);
					SingleStreamUpdater updater = new SingleStreamUpdater(newStream);
					updater.execute();
					CoreController.addStream(streamNameTextField.getText(), newStream);
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

	private class PropertiesFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JLabel errorLabel;
		private final JTextField lsPathTextField;
		private int screenWidth;
		private int screenHeight;
		private JLabel lsPathLabel;
		private JButton applyButton;
		private JButton closeButton;
		private JFrame mainFrame;

		public PropertiesFrame(JFrame frame) {
			super();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			screenWidth = toolkit.getScreenSize().width / 4;
			screenHeight = toolkit.getScreenSize().height / 4;

			//
			this.mainFrame = frame;
			this.setTitle("Properties");
			this.setSize(new Dimension(screenWidth, screenHeight));
			this.setResizable(false);
			this.setLocationRelativeTo(mainFrame);

			Container main = this.getContentPane();
			GridBagLayout layout = new GridBagLayout();
			main.setLayout(layout);

			lsPathLabel = new JLabel("Livestreamer Path:");
			errorLabel = new JLabel();
			errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
			errorLabel.setForeground(Color.RED);
			lsPathTextField = new JTextField(AppPropertiesLoader.getInstance().getAppProperties()
					.getValue(AppPropertiesConstants.LIVESTREAMER_PATH));
			lsPathTextField.setCaretPosition(0);
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
				if (AppPropertiesLoader.getInstance().getAppProperties()
						.checkLivestreamerPath(newLiveStreamerPath)) {
					AppPropertiesLoader
							.getInstance()
							.getAppProperties()
							.setValueAndSaveProperties(AppPropertiesConstants.PROPERTIES_FILE,
									AppPropertiesConstants.LIVESTREAMER_PATH, newLiveStreamerPath);
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

	private class StreamsPanelUpdater extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() {
			statusPanel.removeAll();
			statusPanel.add(BorderLayout.CENTER, statusProgressBar);
			statusPanel.revalidate();
			statusPanel.repaint();

			JPanel buttonsPanel = (JPanel) bottomPanel.getComponent(0);
			buttonsPanel.getComponent(0).setEnabled(false);
			buttonsPanel.getComponent(1).setEnabled(false);
			buttonsPanel.getComponent(2).setEnabled(false);
			buttonsPanel.getComponent(3).setEnabled(false);

			if (CoreController.getStreamStore(String.valueOf(StreamPanelShow.ACTIVE)) != null) {
				CoreController.updateStreamStore();
			}
			else {
				CoreController.initStreamStore();
			}
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
			topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
			topPanel.revalidate();
			topPanel.repaint();
		}
	}

	private class SingleStreamUpdater extends SwingWorker<Void, Void> {
		private Stream streamToUpdate;

		SingleStreamUpdater(Stream stream) {
			this.streamToUpdate = stream;
		}

		@Override
		protected Void doInBackground() throws Exception {
			streamToUpdate.update();
			return null;
		}

		protected void done() {
			topPanel.removeAll();
			topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
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

	private class DeleteButtonAction extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			JPanel parentPanel = (JPanel) button.getParent();
			final JLabel nameLabel = (JLabel) parentPanel.getComponent(1);
			if (createDeleteOptionPane() == 0) {
				CoreController.removeStream(nameLabel.getText());
				topPanel.removeAll();
				topPanel.add(BorderLayout.CENTER, addStreamsPanel(currentSize, currentShow));
				topPanel.revalidate();
				topPanel.repaint();
			}
		}
	}

	private class StartButtonAction extends MouseAdapter {

		StartButtonAction() {
		}

		@SuppressWarnings("unchecked")
		@Override
		public void mouseClicked(MouseEvent e) {
			if (AppPropertiesLoader
					.getInstance()
					.getAppProperties()
					.checkLivestreamerPath(
							AppPropertiesLoader.getInstance().getAppProperties()
									.getValue(AppPropertiesConstants.LIVESTREAMER_PATH))) {
				JButton button = (JButton) e.getSource();
				JPanel parentPanel = (JPanel) button.getParent();
				JLabel nameLabel = (JLabel) parentPanel.getComponent(1);
				final JComboBox<String> qialityBox = (JComboBox<String>) parentPanel.getComponent(5);
				final Stream stream = CoreController.getStream(nameLabel.getText());

				CoreController.startStream(nameLabel.getText(), stream, (String) qialityBox.getSelectedItem());
			}
			else {
				createStatusPanelInfoLabel(StatusType.LIVESTREAMER_NOT_SPECIFIED);
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
