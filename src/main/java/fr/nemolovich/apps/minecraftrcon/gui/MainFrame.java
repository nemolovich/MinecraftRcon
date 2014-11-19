/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui;

import fr.nemolovich.apps.minecraftrcon.ClientSocket;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColors;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsConstants;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsUtil;
import fr.nemolovich.apps.minecraftrcon.gui.command.Command;
import fr.nemolovich.apps.minecraftrcon.gui.command.CommandsUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
			.compile("^.*-{4,}\\s.+:\\s\\(\\d+/(?<nbPages>\\d+)\\)\\s-{4,}.*");
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
	private final ClientSocket socket;
	private final List<String> commandHistory;
	private int currentHistoryIndex;
	private String currentLine;

	/**
	 * Creates new form MainFrame
	 *
	 * @param socket
	 */
	public MainFrame(ClientSocket socket) {
		this.socket = socket;

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
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		outputScroll = new javax.swing.JScrollPane();
		output = new javax.swing.JTextPane();
		commandField = new javax.swing.JTextField();
		copyButton = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		quitButton = new javax.swing.JButton();
		playersButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		stopButton = new javax.swing.JButton();

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
		stopButton.setMaximumSize(new java.awt.Dimension(97, 26));
		stopButton.setMinimumSize(new java.awt.Dimension(97, 26));
		stopButton.setPreferredSize(new java.awt.Dimension(97, 26));
		stopButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stopButtonActionPerformed(evt);
			}
		});

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
										412, Short.MAX_VALUE)
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

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents

	public void close() {
		this.socket.close();
		this.exit(true);
	}

	private void fine(String msg) {
		this.write(msg, Level.FINE);
	}

	private void info(String msg) {
		this.write(msg, Level.INFO);
	}

	private void warning(String msg) {
		this.write(msg, Level.WARNING);
	}

	private void error(String msg) {
		this.write(msg, Level.ERROR);
	}

	private void write(String message, Level level) {

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

	public final void exit() {
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

	private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quitButtonActionPerformed
		if (this.quitAskAction()) {
			exit(true);
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
		try {
			this.fine(String.format("Players list:%n"));
			this.info(this.getRequestResponse("list"));
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
				this.info(this.getRequestResponse("stop"));
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
			this.info(this.getRequestResponse("save-all"));
		} catch (IOException ex) {
			String error = "Can not save the world";
			LOGGER.error(error, ex);
			this.error(String.format("%s%n", error));
		}
	}// GEN-LAST:event_saveButtonActionPerformed

	private void clearConsole() {
		this.output.setText("");
	}

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
					this.info(String.format("%s",
							this.getRequestResponse(command.substring(1))));
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
			}
		}
		if (suggestions.size() == 1) {
			String text = suggestions.get(0);
			if (text.startsWith("/")) {
				this.commandField.setText(text);
			} else {
				this.commandField.setText(String.format("/help %s", text));
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

	private String getRequestResponse(String request) throws IOException {
		int requestId = this.socket.sendRequest(request);
		return this.socket.readResponse(requestId);
	}

	private boolean quitAskAction() {
		return JOptionPane.showConfirmDialog(this,
				"Do you really want to leave?", "Leave client? oO?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton clearButton;
	private javax.swing.JTextField commandField;
	private javax.swing.JButton copyButton;
	private javax.swing.JTextPane output;
	private javax.swing.JScrollPane outputScroll;
	private javax.swing.JButton playersButton;
	private javax.swing.JButton quitButton;
	private javax.swing.JButton saveButton;
	private javax.swing.JButton stopButton;
	// End of variables declaration//GEN-END:variables

}
