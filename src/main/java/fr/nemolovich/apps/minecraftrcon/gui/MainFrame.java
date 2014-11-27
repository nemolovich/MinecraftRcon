/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui;

import fr.nemolovich.apps.minecraftrcon.ClientSocket;
import fr.nemolovich.apps.minecraftrcon.Launcher;
import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.BrowserException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColors;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsConstants;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsUtil;
import fr.nemolovich.apps.minecraftrcon.gui.command.Command;
import fr.nemolovich.apps.minecraftrcon.gui.command.CommandsUtils;
import fr.nemolovich.apps.minecraftrcon.gui.utils.LinkLabel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 *
 * @author Nemolovich
 */
public class MainFrame extends javax.swing.JFrame {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 2901406508413961272L;

	private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

	/*
	 * Resources
	 */
	private static final String FRAME_ICON = "icon/icon.png";
	private static final String RESOURCES_PATH = "/fr/nemolovich/apps/minecraftrcon/gui/";
	/*
	 * Commands
	 */

	private static final Pattern SERVER_BASIC_COMMAND_PATTERN = Pattern
			.compile("\n(?<cmd>/\\w+(-\\w+)*):\\s");
	private static final Pattern SERVER_COMMAND_PAGES_PATTERN = Pattern
			.compile("-{7,}\\s.+:\\s.+\\s\\(\\d+/(?<nbPages>\\d+)\\)\\s-{7,}.*");
	private static final Pattern SERVER_CUSTOM_COMMAND_PATTERN = Pattern
			.compile("\n(?<cmd>\\w+(-\\w+)*):\\s");

	/*
	 * Log styles
	 */
	private static final StyleContext sc = new StyleContext();
	private static final Style DEFAULT_STYLE = sc
			.getStyle(StyleContext.DEFAULT_STYLE);
	private static final Style FINE_STYLE = sc.addStyle("FINE_STYLE",
			DEFAULT_STYLE);
	private static final Style ERROR_STYLE = sc.addStyle("ERROR_STYLE",
			DEFAULT_STYLE);
	private static final Style WARNING_STYLE = sc.addStyle("WARNING_STYLE",
			DEFAULT_STYLE);
	private static final List<Style> MINECRAFT_STYLES;

	static {
		StyleConstants.setForeground(DEFAULT_STYLE, Color.decode("#AAAAAA"));
		StyleConstants.setForeground(FINE_STYLE, Color.decode("#55FF55"));
		StyleConstants.setForeground(ERROR_STYLE, Color.decode("#FF5555"));
		StyleConstants.setForeground(WARNING_STYLE, Color.decode("#FFAA00"));

		MINECRAFT_STYLES = Collections.synchronizedList(new ArrayList());
		Style style;
		for (MinecraftColors color : MinecraftColorsUtil.getColors()) {
			style = sc.addStyle(color.getName().toUpperCase().concat("_STYLE"),
					DEFAULT_STYLE);
			StyleConstants.setForeground(style,
					Color.decode(color.getForegroundColor()));
			MINECRAFT_STYLES.add(style);
		}
	}

	/*
	 * App variables
	 */
	private ClientSocket socket;
	private final List<String> commandHistory;
	private int currentHistoryIndex;
	private String currentLine;

	private final String[] args;

	private final String host;
	private final int port;
	private final String password;

	/**
	 * Creates new form MainFrame
	 *
	 * @param host
	 * @param port
	 * @param password
	 * @param args
	 * @throws AuthenticationException
	 * @throws ConnectionException
	 */
	public MainFrame(String host, int port, String password, String... args)
			throws ConnectionException, AuthenticationException {
		this.host = host;
		this.port = port;
		this.password = password;
		this.args = args;
		this.socket = new ClientSocket(host, port, password);

		this.commandHistory = Collections
				.synchronizedList(new ArrayList<String>());
		this.currentHistoryIndex = -1;

		CommandsUtils
				.addCommand(new CommandAdapter("/cls", "Clear the console") {
					@Override
					public String doCommand(String... args) {
						clearConsole();
						return null;
					}
				});
		CommandsUtils.addCommand(new CommandAdapter("/quit",
				"Leave the rcon application") {
			@Override
			public String doCommand(String... args) {
				new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						error(String
								.format("Closing application in 3 secondes%n"));
						Thread.sleep(3000);
						return null;
					}

					@Override
					protected void done() {
						close();
					}
				}.execute();
				return null;
			}
		});
		CommandsUtils.addCommand(new CommandAdapter("/colors",
				"Display minecraft colors") {
			@Override
			public String doCommand(String... args) {
				for (MinecraftColors color : MinecraftColorsUtil.getColors()) {
					info(String.format("%1$s%2$s%2$s - %3$s",
							MinecraftColorsConstants.MINECRAFT_COLOR_PREFIX,
							color.getCode(), color.getName()));
				}
				return null;
			}
		});

		try {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					MainFrame.class.getResource(RESOURCES_PATH
							.concat(FRAME_ICON))));
		} catch (Exception e) {
			LOGGER.warn("Can not load icon", e);
		}

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (quitAskAction()) {
					exit();
				}
			}
		});

		initComponents();

		this.setConnected();

		setFocusTraversalPolicy(new FocusTraversalPolicy() {

			@Override
			public Component getComponentAfter(Container aContainer,
					Component aComponent) {

				Component result = null;
				if (aComponent.equals(output)) {
					result = copyButton;
				} else if (aComponent.equals(commandField)) {
					result = copyButton;
				} else if (aComponent.equals(copyButton)) {
					result = clearButton;
				} else if (aComponent.equals(clearButton)) {
					result = playersButton;
				} else if (aComponent.equals(playersButton)) {
					result = saveButton;
				} else if (aComponent.equals(saveButton)) {
					result = stopButton;
				} else if (aComponent.equals(stopButton)) {
					result = quitButton;
				} else if (aComponent.equals(quitButton)) {
					result = output;
				}
				return result;
			}

			@Override
			public Component getComponentBefore(Container aContainer,
					Component aComponent) {
				Component result = null;
				if (aComponent.equals(output)) {
					result = quitButton;
				} else if (aComponent.equals(commandField)) {
					result = output;
				} else if (aComponent.equals(copyButton)) {
					result = output;
				} else if (aComponent.equals(clearButton)) {
					result = copyButton;
				} else if (aComponent.equals(playersButton)) {
					result = clearButton;
				} else if (aComponent.equals(saveButton)) {
					result = playersButton;
				} else if (aComponent.equals(stopButton)) {
					result = saveButton;
				} else if (aComponent.equals(quitButton)) {
					result = stopButton;
				}
				return result;
			}

			@Override
			public Component getFirstComponent(Container aContainer) {
				return output;
			}

			@Override
			public Component getLastComponent(Container aContainer) {
				return quitButton;
			}

			@Override
			public Component getDefaultComponent(Container aContainer) {
				return commandField;
			}
		});
		try {
			String helpMsg = this.getRequestResponse("help");
			List<String> serverCommands = this.parseServerCommands(helpMsg);
			CommandsUtils.addServerCommand(serverCommands);
		} catch (IOException ex) {
			String message = "Can not retrieve server commands";
			this.warning(String.format("%s: %s%n", message, ex.getMessage()));
			LOGGER.warn(message, ex);
		}

		this.fine(String
				.format("Connection succeed! Welcome on Nemolovich Minecraft RCON Administration%n"));

	}

	private List<String> parseServerCommands(String msg) {
		String content = parseColorString(msg);
		List<String> commands = getBasicCommands(content);
		commands.addAll(getCustomCommands(content));
		return commands;
	}

	private static String parseColorString(String msg) {
		return msg.replaceAll("\u00A7(\\d|[a-f])", "");
	}

	private List<String> getBasicCommands(String msg) {
		return getBasicCommands(msg, false);
	}

	private List<String> getBasicCommands(String msg, boolean skipPagination) {
		List<String> commands = new ArrayList();
		Matcher matcher = SERVER_BASIC_COMMAND_PATTERN.matcher(msg);

		while (matcher.find()) {
			commands.add(matcher.group("cmd"));
		}
		if (!skipPagination) {
			Matcher multiPage = SERVER_COMMAND_PAGES_PATTERN.matcher(msg
					.replaceAll("\\n", "\\\\n"));
			if (multiPage.matches()) {
				int nbPages = Integer.valueOf(multiPage.group("nbPages"));
				if (nbPages > 1) {
					for (int i = 2; i <= nbPages; i++) {
						commands.addAll(getBasicCommands(
								parseColorString(getNextHelp(i)), true));
					}
				}
			}
		}
		return commands;
	}

	private String getHelp(String command) {
		String result = String.format("Can not retrieve help for %s", command);
		;
		try {
			result = this.getRequestResponse(String.format("help %s", command));
		} catch (IOException ex) {
			LOGGER.error(String.format("Can not retrieve help for command ",
					command), ex);
		}
		return result;
	}

	private String getNextHelp(int index) {
		String result = null;
		try {
			result = this.getRequestResponse(String.format("help %d", index));
		} catch (IOException ex) {
			LOGGER.error(String.format("Can not retrieve help #%d", index), ex);
		}
		return result;
	}

	private List<String> getCustomCommands(String msg) {
		List<String> commands = new ArrayList();
		Matcher matcher = SERVER_CUSTOM_COMMAND_PATTERN.matcher(msg);

		while (matcher.find()) {
			commands.add(matcher.group("cmd"));
		}
		return commands;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		aboutFrame = new javax.swing.JDialog(this);
		headerImage = new javax.swing.JLabel();
		headText = new javax.swing.JLabel();
		versionLabel = new javax.swing.JLabel();
		versionValue = new javax.swing.JLabel();
		closeAboutFrame = new javax.swing.JButton();
		contactLabel = new javax.swing.JLabel();
		contactValue = new LinkLabel("mailto:nemolovich.apps@outlook.com");
		sourcesLabel = new javax.swing.JLabel();
		sourcesValue = new LinkLabel(
				"http://github.com/nemolovich/MinecraftRcon");
		outputScroll = new javax.swing.JScrollPane();
		output = new javax.swing.JTextPane();
		commandField = new javax.swing.JTextField();
		copyButton = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		quitButton = new javax.swing.JButton();
		playersButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		stopButton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		reconnectItem = new javax.swing.JMenuItem();
		disconnectItem = new javax.swing.JMenuItem();
		javax.swing.JPopupMenu.Separator fileSep1 = new javax.swing.JPopupMenu.Separator();
		quitItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		copyItem = new javax.swing.JMenuItem();
		clearItem = new javax.swing.JMenuItem();
		commandMenu = new javax.swing.JMenu();
		playersItem = new javax.swing.JMenuItem();
		javax.swing.JPopupMenu.Separator editSep1 = new javax.swing.JPopupMenu.Separator();
		saveItem = new javax.swing.JMenuItem();
		stopItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		updatesItem = new javax.swing.JMenuItem();
		javax.swing.JPopupMenu.Separator helpSep1 = new javax.swing.JPopupMenu.Separator();
		aboutItem = new javax.swing.JMenuItem();

		aboutFrame.setTitle("About...");
		aboutFrame.setModal(true);
		aboutFrame.setResizable(false);
		aboutFrame.setType(java.awt.Window.Type.POPUP);

		headerImage
				.setIcon(new javax.swing.ImageIcon(
						getClass()
								.getResource(
										"/fr/nemolovich/apps/minecraftrcon/gui/icon/background_400x78.png"))); // NOI18N

		headText.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		headText.setText("Nemolovich Minecraft RCON administration");
		headText.setToolTipText("");

		versionLabel.setFont(new java.awt.Font("Miriam Fixed", 0, 14)); // NOI18N
		versionLabel.setText("Version:");

		versionValue.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		versionValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		versionValue.setText(Launcher.getCurrentVersion());

		closeAboutFrame.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		closeAboutFrame.setText("Close");
		closeAboutFrame.setMaximumSize(new java.awt.Dimension(97, 26));
		closeAboutFrame.setMinimumSize(new java.awt.Dimension(97, 26));
		closeAboutFrame.setPreferredSize(new java.awt.Dimension(97, 26));
		closeAboutFrame.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeAboutFrameActionPerformed(evt);
			}
		});

		contactLabel.setFont(new java.awt.Font("Miriam Fixed", 0, 14)); // NOI18N
		contactLabel.setText("Contact:");

		contactValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		contactValue.setText("nemolovich.apps@outlook.com");
		contactValue.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				contactValueMouseClicked(evt);
			}
		});

		sourcesLabel.setFont(new java.awt.Font("Miriam Fixed", 0, 14)); // NOI18N
		sourcesLabel.setText("Sources:");

		sourcesValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		sourcesValue.setText("http://github.com/nemolovich/MinecraftRcon");
		sourcesValue.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				sourcesValueMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout aboutFrameLayout = new javax.swing.GroupLayout(
				aboutFrame.getContentPane());
		aboutFrame.getContentPane().setLayout(aboutFrameLayout);
		aboutFrameLayout
				.setHorizontalGroup(aboutFrameLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(headerImage,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								aboutFrameLayout
										.createSequentialGroup()
										.addGap(291, 291, 291)
										.addComponent(
												closeAboutFrame,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap())
						.addGroup(
								aboutFrameLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												aboutFrameLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																headText,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																aboutFrameLayout
																		.createSequentialGroup()
																		.addGroup(
																				aboutFrameLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								versionLabel)
																						.addComponent(
																								contactLabel)
																						.addComponent(
																								sourcesLabel))
																		.addGap(18,
																				18,
																				18)
																		.addGroup(
																				aboutFrameLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								contactValue,
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								sourcesValue,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								versionValue,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))))
										.addContainerGap()));
		aboutFrameLayout
				.setVerticalGroup(aboutFrameLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								aboutFrameLayout
										.createSequentialGroup()
										.addComponent(
												headerImage,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												78,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(headText)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												aboutFrameLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																versionLabel)
														.addComponent(
																versionValue))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												aboutFrameLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																contactLabel)
														.addComponent(
																contactValue))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												aboutFrameLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																sourcesLabel)
														.addComponent(
																sourcesValue))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												closeAboutFrame,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));

		aboutFrame.pack();
		aboutFrame.setLocationRelativeTo(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Nemolovich Minecraft RCON Application");
		setMinimumSize(new java.awt.Dimension(650, 475));
		setPreferredSize(new java.awt.Dimension(650, 475));

		outputScroll
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outputScroll
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		outputScroll.setAutoscrolls(true);
		outputScroll.setCursor(new java.awt.Cursor(
				java.awt.Cursor.DEFAULT_CURSOR));

		output.setEditable(false);
		output.setBackground(new java.awt.Color(51, 51, 51));
		output.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		output.setFont(new java.awt.Font("Miriam Fixed", 0, 11)); // NOI18N
		output.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
		outputScroll.setViewportView(output);

		commandField.setFont(new java.awt.Font("Miriam Fixed", 0, 12)); // NOI18N
		commandField.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		commandField.setFocusTraversalKeysEnabled(false);
		commandField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				commandFieldActionPerformed(evt);
			}
		});
		commandField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				commandFieldKeyReleased(evt);
			}
		});

		copyButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		copyButton.setMnemonic('c');
		copyButton.setText("copy");
		copyButton.setToolTipText("Copy the console content");
		copyButton.setMaximumSize(new java.awt.Dimension(97, 26));
		copyButton.setMinimumSize(new java.awt.Dimension(97, 26));
		copyButton.setPreferredSize(new java.awt.Dimension(97, 26));
		copyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyButtonActionPerformed(evt);
			}
		});

		clearButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		clearButton.setMnemonic('l');
		clearButton.setText("clear");
		clearButton.setToolTipText("Clear the console");
		clearButton.setMaximumSize(new java.awt.Dimension(97, 26));
		clearButton.setMinimumSize(new java.awt.Dimension(97, 26));
		clearButton.setPreferredSize(new java.awt.Dimension(97, 26));
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		quitButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		quitButton.setMnemonic('q');
		quitButton.setText("Quit");
		quitButton.setToolTipText("Quit the application");
		quitButton.setMaximumSize(new java.awt.Dimension(97, 26));
		quitButton.setMinimumSize(new java.awt.Dimension(97, 26));
		quitButton.setPreferredSize(new java.awt.Dimension(97, 26));
		quitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				quitButtonActionPerformed(evt);
			}
		});

		playersButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		playersButton.setMnemonic('p');
		playersButton.setText("players");
		playersButton.setToolTipText("Display the players list");
		playersButton.setEnabled(false);
		playersButton.setMaximumSize(new java.awt.Dimension(97, 26));
		playersButton.setMinimumSize(new java.awt.Dimension(97, 26));
		playersButton.setPreferredSize(new java.awt.Dimension(97, 26));
		playersButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playersButtonActionPerformed(evt);
			}
		});

		saveButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		saveButton.setMnemonic('s');
		saveButton.setText("save");
		saveButton.setToolTipText("Save the server world");
		saveButton.setEnabled(false);
		saveButton.setMaximumSize(new java.awt.Dimension(97, 26));
		saveButton.setMinimumSize(new java.awt.Dimension(97, 26));
		saveButton.setPreferredSize(new java.awt.Dimension(97, 26));
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});

		stopButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
		stopButton.setMnemonic('t');
		stopButton.setText("stop");
		stopButton.setToolTipText("Stop the server");
		stopButton.setEnabled(false);
		stopButton.setMaximumSize(new java.awt.Dimension(97, 26));
		stopButton.setMinimumSize(new java.awt.Dimension(97, 26));
		stopButton.setPreferredSize(new java.awt.Dimension(97, 26));
		stopButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stopButtonActionPerformed(evt);
			}
		});

		fileMenu.setMnemonic('F');
		fileMenu.setText("File");

		reconnectItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_R,
				java.awt.event.InputEvent.CTRL_MASK));
		reconnectItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/reconnect.png"))); // NOI18N
		reconnectItem.setMnemonic('R');
		reconnectItem.setText("Reconnect");
		reconnectItem.setToolTipText("Try to reconnect to server");
		reconnectItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reconnectItemActionPerformed(evt);
			}
		});
		fileMenu.add(reconnectItem);

		disconnectItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_D,
				java.awt.event.InputEvent.CTRL_MASK));
		disconnectItem
				.setIcon(new javax.swing.ImageIcon(
						getClass()
								.getResource(
										"/fr/nemolovich/apps/minecraftrcon/gui/icon/disconnect.png"))); // NOI18N
		disconnectItem.setMnemonic('D');
		disconnectItem.setText("Disconnect");
		disconnectItem.setToolTipText("Disconnect from server");
		disconnectItem.setEnabled(false);
		disconnectItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				disconnectItemActionPerformed(evt);
			}
		});
		fileMenu.add(disconnectItem);
		fileMenu.add(fileSep1);

		quitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F4,
				java.awt.event.InputEvent.ALT_MASK));
		quitItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/quit.png"))); // NOI18N
		quitItem.setMnemonic('Q');
		quitItem.setText("Quit");
		quitItem.setToolTipText("Leave the application");
		quitItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				quitItemActionPerformed(evt);
			}
		});
		fileMenu.add(quitItem);

		menuBar.add(fileMenu);

		editMenu.setMnemonic('E');
		editMenu.setText("Edit");

		copyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		copyItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/copy.png"))); // NOI18N
		copyItem.setText("Copy");
		copyItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copyItemActionPerformed(evt);
			}
		});
		editMenu.add(copyItem);

		clearItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_L,
				java.awt.event.InputEvent.CTRL_MASK));
		clearItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/clear.png"))); // NOI18N
		clearItem.setMnemonic('L');
		clearItem.setText("Clear");
		clearItem.setToolTipText("Clear the console");
		clearItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearItemActionPerformed(evt);
			}
		});
		editMenu.add(clearItem);

		menuBar.add(editMenu);

		commandMenu.setMnemonic('m');
		commandMenu.setText("Commands");

		playersItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_P,
				java.awt.event.InputEvent.CTRL_MASK));
		playersItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/plays-list.png"))); // NOI18N
		playersItem.setMnemonic('P');
		playersItem.setText("Players list");
		playersItem.setToolTipText("Display the players list");
		playersItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playersItemActionPerformed(evt);
			}
		});
		commandMenu.add(playersItem);
		commandMenu.add(editSep1);

		saveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		saveItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/world.png"))); // NOI18N
		saveItem.setMnemonic('S');
		saveItem.setText("Save the world");
		saveItem.setToolTipText("Save the server world");
		saveItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveItemActionPerformed(evt);
			}
		});
		commandMenu.add(saveItem);

		stopItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.CTRL_MASK));
		stopItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/stop.png"))); // NOI18N
		stopItem.setMnemonic('T');
		stopItem.setText("Stop the server");
		stopItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stopItemActionPerformed(evt);
			}
		});
		commandMenu.add(stopItem);

		menuBar.add(commandMenu);

		helpMenu.setMnemonic('H');
		helpMenu.setText("Help");
		helpMenu.setToolTipText("");

		updatesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_U,
				java.awt.event.InputEvent.CTRL_MASK));
		updatesItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/update.png"))); // NOI18N
		updatesItem.setMnemonic('U');
		updatesItem.setText("Check for updates");
		updatesItem.setToolTipText("Check if there is a new version available");
		updatesItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updatesItemActionPerformed(evt);
			}
		});
		helpMenu.add(updatesItem);
		helpMenu.add(helpSep1);

		aboutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_H,
				java.awt.event.InputEvent.CTRL_MASK));
		aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/fr/nemolovich/apps/minecraftrcon/gui/icon/about.png"))); // NOI18N
		aboutItem.setText("About...");
		aboutItem.setToolTipText("Display information about application");
		aboutItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutItemActionPerformed(evt);
			}
		});
		helpMenu.add(aboutItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(4, 4, 4)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		copyButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		clearButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		playersButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		saveButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		stopButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		quitButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(commandField)
												.addComponent(
														outputScroll,
														javax.swing.GroupLayout.Alignment.TRAILING))
								.addGap(4, 4, 4)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(4, 4, 4)
								.addComponent(outputScroll,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										389, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commandField,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														copyButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														clearButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														quitButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														playersButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														saveButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														stopButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(4, 4, 4)));

		getAccessibleContext().setAccessibleName("mainFrame");

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents

	private void reconnectItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_reconnectItemActionPerformed
		this.reconnectAction();
	}// GEN-LAST:event_reconnectItemActionPerformed

	private void disconnectItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_disconnectItemActionPerformed
		this.disconnectAction();
	}// GEN-LAST:event_disconnectItemActionPerformed

	private void quitItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quitItemActionPerformed
		if (this.quitAskAction()) {
			this.close();
		}
	}// GEN-LAST:event_quitItemActionPerformed

	private void playersItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_playersItemActionPerformed
		this.playersButtonActionPerformed(null);
	}// GEN-LAST:event_playersItemActionPerformed

	private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveItemActionPerformed
		this.saveButtonActionPerformed(null);
	}// GEN-LAST:event_saveItemActionPerformed

	private void stopItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stopItemActionPerformed
		this.stopButtonActionPerformed(null);
	}// GEN-LAST:event_stopItemActionPerformed

	private void updatesItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updatesItemActionPerformed
		Launcher.checkForUpdates(this.args);
	}// GEN-LAST:event_updatesItemActionPerformed

	private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_aboutItemActionPerformed
		this.aboutFrame.setVisible(true);
	}// GEN-LAST:event_aboutItemActionPerformed

	private void sourcesValueMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_sourcesValueMouseClicked
		this.openLink(((LinkLabel) this.sourcesValue).getLink());
	}// GEN-LAST:event_sourcesValueMouseClicked

	private void contactValueMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_contactValueMouseClicked
		this.openLink(((LinkLabel) this.contactValue).getLink());
	}// GEN-LAST:event_contactValueMouseClicked

	private void closeAboutFrameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeAboutFrameActionPerformed
		this.aboutFrame.setVisible(false);
	}// GEN-LAST:event_closeAboutFrameActionPerformed

	private void copyItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_copyItemActionPerformed
		this.copyButtonActionPerformed(null);
	}// GEN-LAST:event_copyItemActionPerformed

	private void clearItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clearItemActionPerformed
		this.clearButtonActionPerformed(null);
	}// GEN-LAST:event_clearItemActionPerformed

	private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quitButtonActionPerformed
		if (this.quitAskAction()) {
			close();
		}
	}// GEN-LAST:event_quitButtonActionPerformed

	private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_copyButtonActionPerformed
		String selection = this.output.getText();
		if (selection != null && !selection.isEmpty()) {
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(new StringSelection(selection), null);
		}
	}// GEN-LAST:event_copyButtonActionPerformed

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clearButtonActionPerformed
		this.clearConsole();
	}// GEN-LAST:event_clearButtonActionPerformed

	private void playersButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_playersButtonActionPerformed
		this.fine(String.format("Players list:%n"));
		try {
			// this.info(this.getRequestResponse("list"));
			this.parrallelInfo("%s", "list");
		} catch (IOException ex) {
			String error = "Can not retrieve players list";
			LOGGER.error(error, ex);
			this.error(String.format("%s%n", error));
		}
	}// GEN-LAST:event_playersButtonActionPerformed

	private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stopButtonActionPerformed
		if (JOptionPane.showConfirmDialog(this,
				"Do you really want to stop the server?",
				"Stop the server? :o", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
			try {
				this.error(String.format("Stop server:%n"));
				// this.info(this.getRequestResponse("stop"));
				this.parrallelInfo("%s", "stop");
			} catch (IOException ex) {
				String error = "Can not stop the server";
				LOGGER.error(error, ex);
				this.error(String.format("%s%n", error));
			}
		}
	}// GEN-LAST:event_stopButtonActionPerformed

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed
		try {
			this.warning(String.format("Save world:%n"));
			// this.info(this.getRequestResponse("save-all"));
			this.parrallelInfo("%s", "save-all");
		} catch (IOException ex) {
			String error = "Can not save the world";
			LOGGER.error(error, ex);
			this.error(String.format("%s%n", error));
		}
	}// GEN-LAST:event_saveButtonActionPerformed

	private void commandFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_commandFieldActionPerformed
		String command = this.commandField.getText();
		String[] args = command.split(" ");
		String commandName = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);
		this.info(String.format("%s%n", command));
		this.commandField.setText("");
		if (!command.isEmpty()) {
			if (CommandsUtils.getInternalCommands().contains(commandName)) {
				Command c = CommandsUtils.getInternalCommand(commandName);
				c.doCommand(args);
			} else if (commandName.equals("/help")
					&& args.length > 0
					&& CommandsUtils.getInternalCommands().contains(
							String.format("/%s", args[0]))) {
				this.info(String.format("%s%n", CommandsUtils
						.getInternalCommandHelp(String.format("/%s", args[0]))));
			} else {
				try {
					// this.info(String.format("%s",
					// this.getRequestResponse(command.substring(1))));
					this.parrallelInfo("%s", command.substring(1));
				} catch (IOException ex) {
					String errorMessage = String.format(
							"Communication error: %s%n", ex.getMessage());
					this.error(errorMessage);
					LOGGER.error(errorMessage, ex);
				}
			}
			this.commandHistory.add(command);
			this.currentHistoryIndex = this.commandHistory.size();
		}
	}// GEN-LAST:event_commandFieldActionPerformed

	private void commandFieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_commandFieldKeyReleased
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_TAB) {
			this.suggest();
		} else if (code == KeyEvent.VK_DOWN) {
			this.selectNextCommand();
		} else if (code == KeyEvent.VK_UP) {
			this.selectPreviousCommand();
		} else {
			if (this.currentHistoryIndex >= this.commandHistory.size()) {
				this.currentLine = this.commandField.getText();
			}
		}
	}// GEN-LAST:event_commandFieldKeyReleased

	private void selectPreviousCommand() {
		if (this.currentHistoryIndex >= 1 && this.commandHistory.size() > 0) {
			this.currentHistoryIndex--;
			this.commandField.setText(this.commandHistory
					.get(this.currentHistoryIndex));
		}
	}

	private void selectNextCommand() {
		if (this.currentHistoryIndex > -1
				&& this.currentHistoryIndex + 1 < this.commandHistory.size()) {
			this.currentHistoryIndex++;
			this.commandField.setText(this.commandHistory
					.get(this.currentHistoryIndex));
		} else {
			this.commandField.setText(this.currentLine);
			this.currentHistoryIndex = this.commandHistory.size();
		}
	}

	private void suggest() {
		String line = this.commandField.getText();
		List<String> suggestions = new ArrayList();

		List<String> availableCommands = CommandsUtils.getAvailableCommands();
		for (String cmd : availableCommands) {
			if (cmd.startsWith(line)) {
				suggestions.add(String.format("%s ", cmd));
			} else if (line.startsWith("/help ")) {
				String hLine = line.substring("/help ".length());
				if (cmd.substring(1).startsWith(hLine)) {
					suggestions.add(String.format("%s", cmd.substring(1)));
				}
			}
		}
		if (suggestions.size() == 1) {
			String text = suggestions.get(0);
			if (text.startsWith("/")) {
				this.commandField.setText(text);
			} else {
				this.commandField.setText(String.format("/help %s ", text));
			}
		} else if (suggestions.size() > 1
				&& suggestions.size() < availableCommands.size()) {
			StringBuilder display = new StringBuilder();
			display.append(String.format("Available commmands (%d/%d):%n",
					suggestions.size(), availableCommands.size()));
			for (String cmd : suggestions) {
				display.append(String.format("\t%s%n", cmd));
			}
			this.info(display.toString());
		}
	}

	private String getRequestResponse(final String request) throws IOException {
		String result = null;
		try {
			int requestId = socket.sendRequest(request);
			result = socket.readResponse(requestId);
		} catch (IOException se) {
			if (se instanceof SocketException) {
				setDisconnected();
			}
			throw se;
		}
		return result;
	}

	private void openLink(String link) {
		Exception error = null;
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(link));
			} catch (IOException | URISyntaxException ex) {
				error = ex;
			}
		} else {
			error = new BrowserException();
		}
		if (error != null) {
			JXErrorPane.showDialog(null, new ErrorInfo("Open link error",
					"Can not open link", null, "Error", error,
					java.util.logging.Level.SEVERE, null));
		}
	}

	private void exit() {
		exit(false);
	}

	public final void exit(boolean forced) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(false);
		this.dispose();
		if (forced) {
			System.exit(0);
		}
	}

	public void close() {
		this.socket.close();
		this.exit(true);
	}

	private void clearConsole() {
		this.output.setText("");
	}

	private boolean quitAskAction() {
		return JOptionPane.showConfirmDialog(this,
				"Do you really want to leave?", "Leave client? oO?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	public void disconnectAction() {
		socket.close();
		setDisconnected();
	}

	private void reconnectAction() {
		LOGGER.info("Trying to reconnect to the socket");
		try {
			this.socket = new ClientSocket(this.host, this.port, this.password);
			this.fine(String
					.format("Connection succeed! Welcome on Nemolovich Minecraft RCON Administration%n"));
		} catch (ConnectionException | AuthenticationException e) {
			this.error(String.format("Can not reconnect to server%n"));
		}
		this.setConnected();
	}

	private void setDisconnected() {
		this.warning(String.format("Disconnected from server%n"));
		this.setState(false);
	}

	private void setConnected() {
		this.setState(true);
	}

	private void setState(boolean action) {
		if (this.socket.isClosed() ^ action) {
			this.playersButton.setEnabled(action);
			this.playersItem.setEnabled(action);
			this.saveButton.setEnabled(action);
			this.saveItem.setEnabled(action);
			this.stopButton.setEnabled(action);
			this.stopItem.setEnabled(action);
			this.disconnectItem.setEnabled(action);
			this.reconnectItem.setEnabled(!action);
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JDialog aboutFrame;
	private javax.swing.JMenuItem aboutItem;
	private javax.swing.JButton clearButton;
	private javax.swing.JMenuItem clearItem;
	private javax.swing.JButton closeAboutFrame;
	private javax.swing.JTextField commandField;
	private javax.swing.JMenu commandMenu;
	private javax.swing.JLabel contactLabel;
	private javax.swing.JLabel contactValue;
	private javax.swing.JButton copyButton;
	private javax.swing.JMenuItem copyItem;
	private javax.swing.JMenuItem disconnectItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JLabel headText;
	private javax.swing.JLabel headerImage;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JTextPane output;
	private javax.swing.JScrollPane outputScroll;
	private javax.swing.JButton playersButton;
	private javax.swing.JMenuItem playersItem;
	private javax.swing.JButton quitButton;
	private javax.swing.JMenuItem quitItem;
	private javax.swing.JMenuItem reconnectItem;
	private javax.swing.JButton saveButton;
	private javax.swing.JMenuItem saveItem;
	private javax.swing.JLabel sourcesLabel;
	private javax.swing.JLabel sourcesValue;
	private javax.swing.JButton stopButton;
	private javax.swing.JMenuItem stopItem;
	private javax.swing.JMenuItem updatesItem;
	private javax.swing.JLabel versionLabel;
	private javax.swing.JLabel versionValue;

	// End of variables declaration//GEN-END:variables

	private void fine(String msg) {
		this.write(msg, Level.FINE);
	}

	private void info(String msg) {
		this.write(msg, Level.INFO);
	}

	private void parrallelInfo(final String format, final String request)
			throws IOException {
		new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				try {
					String result = getRequestResponse(request);
					info(String.format(format, result));
				} catch (IOException ex) {
					throw ex;
				}
				return null;
			}
		}.execute();
	}

	private void warning(String msg) {
		this.write(msg, Level.WARNING);
	}

	private void error(String msg) {
		this.write(msg, Level.ERROR);
	}

	private synchronized void write(String message, Level level) {

		Style style;
		switch (level) {
		case INFO:
			style = DEFAULT_STYLE;
			break;
		case FINE:
			style = FINE_STYLE;
			break;
		case WARNING:
			style = WARNING_STYLE;
			break;
		case ERROR:
			style = ERROR_STYLE;
			break;
		default:
			style = DEFAULT_STYLE;
			break;
		}

		try {
			if (!message.startsWith("/")
					& message
							.contains(MinecraftColorsConstants.MINECRAFT_COLOR_PREFIX)) {
				String[] parts = message
						.split(MinecraftColorsConstants.MINECRAFT_COLOR_PREFIX);
				for (String part : parts) {
					if (!part.isEmpty()) {
						MinecraftColors color = MinecraftColorsUtil
								.getColorFromCode(part.charAt(0));
						Style colorStyle = style;
						if (color != null) {
							Style mcStyle = sc.getStyle(color.getName()
									.toUpperCase().concat("_STYLE"));
							if (mcStyle != null) {
								colorStyle = mcStyle;
							}
						}
						this.output.getDocument().insertString(
								this.output.getDocument().getLength(),
								part.substring(1), colorStyle);
					}
				}
				this.output.getDocument().insertString(
						this.output.getDocument().getLength(),
						String.format("%n"), style);
			} else {
				this.output.getDocument().insertString(
						this.output.getDocument().getLength(), message, style);
			}
			this.output.setCaretPosition(this.output.getDocument().getLength());
		} catch (BadLocationException ex) {
			LOGGER.error("GUI Log error", ex);
		}

	}

}
