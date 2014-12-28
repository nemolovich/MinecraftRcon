/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui;

import fr.nemolovich.apps.minecraftrcon.Launcher;
import fr.nemolovich.apps.minecraftrcon.config.GlobalConfig;
import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.BrowserException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColors;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsConstants;
import fr.nemolovich.apps.minecraftrcon.gui.colors.MinecraftColorsUtil;
import fr.nemolovich.apps.minecraftrcon.gui.command.Command;
import fr.nemolovich.apps.minecraftrcon.gui.command.CommandConstants;
import fr.nemolovich.apps.minecraftrcon.gui.command.CommandsUtils;
import fr.nemolovich.apps.minecraftrcon.gui.components.Button;
import fr.nemolovich.apps.minecraftrcon.gui.table.frame.TableFrameModel;
import fr.nemolovich.apps.minecraftrcon.gui.table.listener.CommandListSelectionListener;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.CommandsTableModel;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.CustomTableModel;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.PlayersIPTableModel;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.PlayersTableModel;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.TableModelManager;
import fr.nemolovich.apps.minecraftrcon.gui.utils.LinkLabel;
import fr.nemolovich.apps.minecraftrcon.socket.ClientSocket;
import fr.nemolovich.apps.minecraftrcon.socket.PingThread;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
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
     * Commands regex patterns
     */
    private static final Pattern SERVER_BASIC_COMMAND_PATTERN = Pattern
            .compile("\n(?<cmd>/\\w+(-\\w+)*):\\s");
    public static final Pattern SERVER_COMMAND_PAGES_PATTERN = Pattern
            .compile("-{7,}\\s.+:\\s.+\\s\\(\\d+/(?<nbPages>\\d+)\\)\\s-{7,}.*");
    private static final Pattern SERVER_CUSTOM_COMMAND_PATTERN = Pattern
            .compile("\n(?<cmd>\\w+(-\\w+)*):\\s");
    private static final String PLAYER_NAME_PATTERN = "(?<playerName>[^\\n]+)";
    private static final String PLAYER_IP_PATTERN = "(?<playerIP>\\[\\d{1,3}(\\.\\d{1,3}){3}\\])";
    private static final Pattern PLAYER_IP_CLEANER = Pattern.compile("(?<otherChar>[^\\d\\.])");
    private static final Pattern PLAYERS_LIST_IP_PATTERN = Pattern
            .compile(String.format("(?:(?<line>%s\\s+%s*)\\n)",
            PLAYER_NAME_PATTERN, PLAYER_IP_PATTERN));
    private static final Pattern PLAYERS_LIST_PATTERN = Pattern
            .compile("(?:(?<line>[^\\n]*)\\n)");

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

    /*
     * Static variable
     */
    private static final String WRONG_REGEX_PATTERN = "Wrong regex pattern: %s";

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
     * Fonts
     */
    public static final Font MIRIAM_FONT_NORMAL_BOLD = new Font("Miriam Fixed", Font.BOLD, 14);
    private static final Font MIRIAM_FONT_NORMAL = new Font("Miriam Fixed", Font.PLAIN, 14);
    private static final Font MIRIAM_FONT_SMALL_BOLD = new Font("Miriam Fixed", Font.BOLD, 12);
    public static final Font MIRIAM_FONT_SMALL = new Font("Miriam Fixed", Font.PLAIN, 12);
    private static final Font MIRIAM_FONT_TINY_BOLD = new Font("Miriam Fixed", Font.BOLD, 11);
    private static final Font MIRIAM_FONT_TINY = new Font("Miriam Fixed", Font.PLAIN, 11);

    /*
     * PING
     */
    private static final int PING_DELAY = PingThread.DEFAULT_DELAY;

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
    private final Border fieldBorder;
    private final Color fieldColor;
    private final ParallelTask updatePlayersListTask;
    private String playersListCommand;

    /**
     * Creates new form MainFrame.
     *
     * @param host {@link String} - The host to attempt to connect.
     * @param port {@link Integer int} - The host port.
     * @param password {@link  String} - The authentication password.
     * @param args {@link String}[] - The optional parameters.
     */
    public MainFrame(final String host, final int port, final String password,
            String... args) {
        Thread.currentThread().setName("Mainframe-Thread");
        this.host = host;
        this.port = port;
        this.password = password;
        this.args = args;
        this.commandHistory = Collections
                .synchronizedList(new ArrayList<String>());
        this.currentHistoryIndex = -1;

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

        this.initComponents();

        this.customInitComponents();

        if ((Boolean) GlobalConfig.getInstance().get(
                GlobalConfig.PLAYERS_IP_AVAILABLE)) {
            this.playersListCommand = GlobalConfig.getInstance()
                    .getProperty(GlobalConfig.PLAYERS_IP_COMMAND);
            this.updatePlayersListTask = new ParallelTask() {
                @Override
                protected Object runTask() throws Exception {
                    try {
                        String resp = getRequestResponse(playersListCommand);
                        for (Entry<String, String> entry
                                : parsePlayersWithIP(resp).entrySet()) {
                            ((PlayersIPTableModel) dynamicFrameList.getModel())
                                    .addPlayer(entry.getKey(), entry.getValue());
                        }
                        ((PlayersIPTableModel) dynamicFrameList.getModel())
                                .addPlayer("P1", "192.168.1.101");
                        ((PlayersIPTableModel) dynamicFrameList.getModel())
                                .addPlayer("P2", "192.168.1.102");
                        write(resp, Level.INFO, dynamicFrameHelpPane);
                        dynamicFrameHelpPane.setCaretPosition(0);
                    } finally {
                    }
                    return null;
                }
            };
        } else {
            this.playersListCommand = CommandConstants.PLAYERS_LIST_COMMAND;
            this.updatePlayersListTask = new ParallelTask() {
                @Override
                protected Object runTask() throws Exception {
                    String resp = getRequestResponse(playersListCommand);
                    for (String player : parsePlayers(resp)) {
                        ((PlayersTableModel) dynamicFrameList.getModel())
                                .addPlayer(player);
                    }
                    write(resp, Level.INFO, dynamicFrameHelpPane);
                    dynamicFrameHelpPane.setCaretPosition(0);
                    return null;
                }
            };
        }

        this.setVisible(true);
        this.fieldBorder = this.dynamicFrameFilterField.getBorder();
        this.fieldColor = this.dynamicFrameFilterField.getForeground();
    }

    public synchronized void attemptConnect()
            throws ConnectionException, AuthenticationException {
        this.setHostText(this.host);
        this.setDisconnected();
        this.setInProgress();
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setName("Connection-Thread");
                try {
                    socket = new ClientSocket(host, port, password);
                } catch (ConnectionException | AuthenticationException ex) {
                    error(String.format("Connection failed%n"));
                    setDisconnected();
                    catchException(ex);
                    return null;
                }
                if (socket != null) {
                    try {
                        initServerComponents();
                        setConnected();
                        fine(String
                                .format("Connection succeed! Welcome on Nemolovich Minecraft RCON Administration%n"));

                        PingThread.getInstance().setSocket(socket);
                        PingThread.getInstance().setDelay(PING_DELAY);
                        PingThread.getInstance().setFailAction(new SwingWorker() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                error(String.format(
                                        "The connection seems to be lost%n"));
                                socket.close();
                                setDisconnected();
                                return null;
                            }
                        });
                        PingThread.getInstance().enable();
                    } catch (Exception e) {
                        error(String.format("Connection failed%n"));
                        setDisconnected();
                        catchException(e);
                    }
                }
                return null;
            }
        }.execute();
    }

    private List<String> parseServerCommands(String msg) {
        String content = parseColorString(msg);
        List<String> commands = getBasicCommands(content);
        commands.addAll(getCustomCommands(content));
        return commands;
    }

    public static Map<String, String> parsePlayersWithIP(String response) {
        Map<String, String> result = new HashMap<>();

        if (response.contains("\n")) {
            String resp = parseColorString(response.substring(response.indexOf("\n") + 1));
            Matcher matcher = PLAYERS_LIST_IP_PATTERN.matcher(resp);

            String playerName;
            String playerIP;
            while (matcher.find()) {
                if (!matcher.group("line").isEmpty()) {
                    playerName = matcher.group("playerName").trim();
                    playerIP = matcher.group("playerIP").trim();
                    Matcher m = PLAYER_IP_CLEANER.matcher(playerIP);
                    while (m.find()) {
                        playerIP = playerIP.replace(m.group("otherChar"), "");
                    }
                    if (!playerName.isEmpty()
                            && !playerName.equalsIgnoreCase("\n")) {
                        result.put(playerName, playerIP);
                    }
                }
            }
        }
        return result;
    }

    public static List<String> parsePlayers(String response) {
        List<String> result = new ArrayList<>();

        if (response.contains("\n")) {
            String resp = parseColorString(
                    response.substring(response.indexOf("\n") + 1));
            Matcher matcher = PLAYERS_LIST_PATTERN.matcher(resp);

            String playerName;
            while (matcher.find()) {
                playerName = matcher.group("line");
                if (!playerName.isEmpty() && !playerName.equalsIgnoreCase("\n")) {
                    result.add(playerName);
                }
            }
        }
        return result;
    }

    private static String parseColorString(String msg) {
        return msg != null ? msg.replaceAll("\u00A7(\\d|[a-f])", "") : "";
    }

    private List<String> getBasicCommands(String msg) {
        return getBasicCommands(msg, false);
    }

    private List<String> getBasicCommands(String msg, boolean skipPagination) {
        List<String> commands = new ArrayList<>();
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
        String result = null;

        try {
            result = this.getRequestResponse(String.format("%s %s",
                    CommandConstants.HELP_COMMAND, command));
        } catch (IOException ex) {
            LOGGER.error(String.format("Can not retrieve help for command %s",
                    command), ex);
        }
        return result;
    }

    private String getNextHelp(int index) {
        return getNextHelp(index, "");
    }

    private String getNextHelp(int index, String command) {
        String result = null;
        try {
            result = this.getRequestResponse(String.format("%s %s%d",
                    CommandConstants.HELP_COMMAND, command, index));
        } catch (IOException ex) {
            LOGGER.error(String.format("Can not retrieve help %s#%d", command,
                    index), ex);
        }
        return result;
    }

    private List<String> getCustomCommands(String msg) {
        List<String> commands = new ArrayList<>();
        Matcher matcher = SERVER_CUSTOM_COMMAND_PATTERN.matcher(msg);

        while (matcher.find()) {
            commands.add(matcher.group("cmd"));
        }
        return commands;
    }

    /**
     * Initialize components with the server informations.
     */
    private void initServerComponents() {

        this.clear();

        try {
            LOGGER.info("Retreiving server commands...");
            String helpMsg = this.getRequestResponse(CommandConstants.HELP_COMMAND);
            List<String> serverCommands = this.parseServerCommands(helpMsg);
            CommandsUtils.addServerCommand(serverCommands);
            if (serverCommands.isEmpty()) {
                LOGGER.warn("No server commands found");
            } else {
                LOGGER.info(String.format("%d server commands retreived",
                        serverCommands.size()));
            }
        } catch (IOException ex) {
            String message = "Can not retrieve server commands";
            this.warning(String.format("%s: %s%n", message, ex.getMessage()));
            LOGGER.warn(message, ex);
        }

        for (String command : CommandsUtils.getAvailableCommands()) {
            ((CommandsTableModel) this.dynamicFrameList.getModel()).addCommand(command);
        }

    }

    /**
     * Initialize the components with custom properties.
     */
    private void customInitComponents() {
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

        ListSelectionModel listSelectionModel = this.dynamicFrameList
                .getSelectionModel();
        listSelectionModel
                .addListSelectionListener(new CommandListSelectionListener(
                this.dynamicFrameList, new ParallelTask() {
            @Override
            protected Object runTask() throws Exception {
                String command = (String) this.getValue();
                selectHelpRow(command);
                return null;
            }
        }));

        Button sendPlayerMsg = new Button("Send message");
        sendPlayerMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Send message!");
            }
        });
        sendPlayerMsg.setWidth(145);
        TableModelManager.getPlayersFrame().addButton(sendPlayerMsg);
        Button promotePlayerButton = new Button("Promote");
        promotePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("White list!");
            }
        });
        TableModelManager.getPlayersFrame().addButton(promotePlayerButton);
        Button banPlayerButton = new Button("Ban");
        banPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Ban list!");
            }
        });
        TableModelManager.getPlayersFrame().addButton(banPlayerButton);
    }

    /**
     * Initialize the main frame components.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutFrame = new javax.swing.JDialog(this);
        headerImage = new javax.swing.JLabel();
        javax.swing.JLabel headText = new javax.swing.JLabel();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        versionValue = new javax.swing.JLabel();
        closeAboutFrame = new javax.swing.JButton();
        javax.swing.JLabel contactLabel = new javax.swing.JLabel();
        contactValue = new LinkLabel("mailto:nemolovich.apps@outlook.com");
        javax.swing.JLabel sourcesLabel = new javax.swing.JLabel();
        sourcesValue = new LinkLabel("http://github.com/nemolovich/MinecraftRcon");
        dynamicFrame = new javax.swing.JDialog(this);
        dynamicFrameHeader = new javax.swing.JLabel();
        dynamicFrameFilterField = new javax.swing.JTextField();
        dynamicFrameFilterLabel = new javax.swing.JLabel();
        dynamicFrameClearFilterButton = new javax.swing.JButton();
        dynamicFrameListScroll = new javax.swing.JScrollPane();
        dynamicFrameList = TableModelManager.getCommandsFrame().getTable();
        dynamicFrameHelpLabel = new javax.swing.JLabel();
        dynamicFrameHelpScroll = new javax.swing.JScrollPane();
        dynamicFrameHelpPane = new javax.swing.JTextPane();
        dynamicFrameStatusLabel = new javax.swing.JLabel();
        dynamicFrameButtonPanel = new javax.swing.JPanel();
        dynamicFrameButton1 = new javax.swing.JButton();
        dynamicFrameButton2 = new javax.swing.JButton();
        dynamicFrameButton3 = new javax.swing.JButton();
        closedynamicFrame = new javax.swing.JButton();
        outputScroll = new javax.swing.JScrollPane();
        output = new javax.swing.JTextPane();
        commandField = new javax.swing.JTextField();
        copyButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        playersButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        informationPanel = new javax.swing.JPanel();
        informationLabel = new javax.swing.JLabel();
        informationHostLabel = new javax.swing.JLabel();
        informationProgress = new javax.swing.JProgressBar();
        connectionStatus = new javax.swing.JLabel();
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
        playersMenu = new javax.swing.JMenu();
        playersItem = new javax.swing.JMenuItem();
        whiteListItem = new javax.swing.JMenuItem();
        banListItem = new javax.swing.JMenuItem();
        commandsListItem = new javax.swing.JMenuItem();
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

        headerImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/background_400x78.png"))); // NOI18N

        headText.setFont(MIRIAM_FONT_NORMAL_BOLD);
        headText.setText("Nemolovich Minecraft RCON administration");
        headText.setToolTipText("");

        versionLabel.setFont(MIRIAM_FONT_NORMAL);
        versionLabel.setText("Version:");

        versionValue.setFont(MIRIAM_FONT_NORMAL_BOLD);
        versionValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        versionValue.setText(Launcher.getCurrentVersion());

        closeAboutFrame.setFont(MIRIAM_FONT_NORMAL_BOLD);
        closeAboutFrame.setText("Close");
        closeAboutFrame.setMaximumSize(new java.awt.Dimension(97, 26));
        closeAboutFrame.setMinimumSize(new java.awt.Dimension(97, 26));
        closeAboutFrame.setPreferredSize(new java.awt.Dimension(97, 26));
        closeAboutFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAboutFrameActionPerformed(evt);
            }
        });

        contactLabel.setFont(MIRIAM_FONT_NORMAL);
        contactLabel.setText("Contact:");

        contactValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        contactValue.setText("nemolovich.apps@outlook.com");
        contactValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactValueMouseClicked(evt);
            }
        });

        sourcesLabel.setFont(MIRIAM_FONT_NORMAL);
        sourcesLabel.setText("Sources:");

        sourcesValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sourcesValue.setText("http://github.com/nemolovich/MinecraftRcon");
        sourcesValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sourcesValueMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout aboutFrameLayout = new javax.swing.GroupLayout(aboutFrame.getContentPane());
        aboutFrame.getContentPane().setLayout(aboutFrameLayout);
        aboutFrameLayout.setHorizontalGroup(
            aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(aboutFrameLayout.createSequentialGroup()
                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutFrameLayout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addComponent(closeAboutFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(aboutFrameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(headText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(aboutFrameLayout.createSequentialGroup()
                                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(versionLabel)
                                    .addComponent(contactLabel)
                                    .addComponent(sourcesLabel))
                                .addGap(18, 18, 18)
                                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contactValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sourcesValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(versionValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        aboutFrameLayout.setVerticalGroup(
            aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutFrameLayout.createSequentialGroup()
                .addComponent(headerImage, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(headText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(versionValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contactLabel)
                    .addComponent(contactValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aboutFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourcesLabel)
                    .addComponent(sourcesValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeAboutFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        aboutFrame.pack();
        aboutFrame.setLocationRelativeTo(null);

        dynamicFrame.setTitle("List of available commands");
        dynamicFrame.setMinimumSize(new java.awt.Dimension(480, 400));
        dynamicFrame.setModal(true);
        dynamicFrame.setPreferredSize(new java.awt.Dimension(500, 465));
        dynamicFrame.setType(java.awt.Window.Type.POPUP);

        dynamicFrameHeader.setFont(MIRIAM_FONT_NORMAL_BOLD);
        dynamicFrameHeader.setText("List of server available commands:");

        dynamicFrameFilterField.setFont(MIRIAM_FONT_SMALL);
        dynamicFrameFilterField.setToolTipText("Filter the commands list (Regular expressions can be used)");
        dynamicFrameFilterField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        dynamicFrameFilterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dynamicFrameFilterFieldActionPerformed(evt);
            }
        });
        dynamicFrameFilterField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dynamicFrameFilterFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dynamicFrameFilterFieldKeyTyped(evt);
            }
        });

        dynamicFrameFilterLabel.setDisplayedMnemonic('F');
        dynamicFrameFilterLabel.setFont(MIRIAM_FONT_SMALL_BOLD);
        dynamicFrameFilterLabel.setLabelFor(dynamicFrameFilterField);
        dynamicFrameFilterLabel.setText("Filter:");
        dynamicFrameFilterLabel.setToolTipText("");

        dynamicFrameClearFilterButton.setBackground(new java.awt.Color(255, 255, 255));
        dynamicFrameClearFilterButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        dynamicFrameClearFilterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/clear.png"))); // NOI18N
        dynamicFrameClearFilterButton.setToolTipText("Clear the filter field");
        dynamicFrameClearFilterButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dynamicFrameClearFilterButton.setMaximumSize(new java.awt.Dimension(20, 20));
        dynamicFrameClearFilterButton.setMinimumSize(new java.awt.Dimension(20, 20));
        dynamicFrameClearFilterButton.setPreferredSize(new java.awt.Dimension(20, 20));
        dynamicFrameClearFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dynamicFrameClearFilterButtonActionPerformed(evt);
            }
        });

        dynamicFrameListScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        dynamicFrameListScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        dynamicFrameListScroll.setPreferredSize(new java.awt.Dimension(19, 156));
        dynamicFrameListScroll.setViewportView(dynamicFrameList);

        dynamicFrameHelpLabel.setFont(MIRIAM_FONT_NORMAL_BOLD);
        dynamicFrameHelpLabel.setText("Command help:");

        dynamicFrameHelpScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        dynamicFrameHelpScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        dynamicFrameHelpPane.setEditable(false);
        dynamicFrameHelpPane.setBackground(new java.awt.Color(51, 51, 51));
        dynamicFrameHelpPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        dynamicFrameHelpPane.setFont(new java.awt.Font("Miriam Fixed", 0, 11)); // NOI18N
        dynamicFrameHelpPane.setForeground(new java.awt.Color(170, 170, 170));
        dynamicFrameHelpPane.setAutoscrolls(false);
        dynamicFrameHelpPane.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        dynamicFrameHelpScroll.setViewportView(dynamicFrameHelpPane);

        dynamicFrameStatusLabel.setFont(MIRIAM_FONT_SMALL);
        dynamicFrameStatusLabel.setMaximumSize(new java.awt.Dimension(7, 16));
        dynamicFrameStatusLabel.setMinimumSize(new java.awt.Dimension(7, 16));
        dynamicFrameStatusLabel.setPreferredSize(new java.awt.Dimension(7, 16));

        dynamicFrameButtonPanel.setMinimumSize(new java.awt.Dimension(317, 26));
        dynamicFrameButtonPanel.setPreferredSize(new java.awt.Dimension(317, 26));

        dynamicFrameButton1.setFont(MIRIAM_FONT_NORMAL_BOLD);
        dynamicFrameButton1.setText("butt1");
        dynamicFrameButton1.setMaximumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton1.setMinimumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton1.setPreferredSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton1.setVisible(false);

        dynamicFrameButton2.setFont(MIRIAM_FONT_NORMAL_BOLD);
        dynamicFrameButton2.setText("butt2");
        dynamicFrameButton2.setMaximumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton2.setMinimumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton2.setPreferredSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton2.setVisible(false);

        dynamicFrameButton3.setFont(MIRIAM_FONT_NORMAL_BOLD);
        dynamicFrameButton3.setText("butt3");
        dynamicFrameButton3.setMaximumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton3.setMinimumSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton3.setPreferredSize(new java.awt.Dimension(97, 26));
        dynamicFrameButton3.setVisible(false);

        javax.swing.GroupLayout dynamicFrameButtonPanelLayout = new javax.swing.GroupLayout(dynamicFrameButtonPanel);
        dynamicFrameButtonPanel.setLayout(dynamicFrameButtonPanelLayout);
        dynamicFrameButtonPanelLayout.setHorizontalGroup(
            dynamicFrameButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dynamicFrameButtonPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(dynamicFrameButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dynamicFrameButtonPanelLayout.setVerticalGroup(
            dynamicFrameButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dynamicFrameButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(dynamicFrameButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dynamicFrameButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dynamicFrameButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closedynamicFrame.setFont(MIRIAM_FONT_NORMAL_BOLD);
        closedynamicFrame.setMnemonic('C');
        closedynamicFrame.setText("Close");
        closedynamicFrame.setToolTipText("Close frame");
        closedynamicFrame.setMaximumSize(new java.awt.Dimension(97, 26));
        closedynamicFrame.setMinimumSize(new java.awt.Dimension(97, 26));
        closedynamicFrame.setPreferredSize(new java.awt.Dimension(97, 26));
        closedynamicFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closedynamicFrameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dynamicFrameLayout = new javax.swing.GroupLayout(dynamicFrame.getContentPane());
        dynamicFrame.getContentPane().setLayout(dynamicFrameLayout);
        dynamicFrameLayout.setHorizontalGroup(
            dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dynamicFrameLayout.createSequentialGroup()
                .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dynamicFrameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dynamicFrameHelpScroll))
                    .addGroup(dynamicFrameLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dynamicFrameLayout.createSequentialGroup()
                                .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(dynamicFrameHelpLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(dynamicFrameLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(dynamicFrameFilterLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dynamicFrameFilterField)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dynamicFrameClearFilterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dynamicFrameListScroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dynamicFrameHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addComponent(dynamicFrameStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(dynamicFrameLayout.createSequentialGroup()
                        .addComponent(dynamicFrameButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closedynamicFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        dynamicFrameLayout.setVerticalGroup(
            dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dynamicFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dynamicFrameHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dynamicFrameClearFilterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dynamicFrameFilterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dynamicFrameFilterLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameListScroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameHelpLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameHelpScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dynamicFrameStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dynamicFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(closedynamicFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dynamicFrameButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        dynamicFrame.pack();
        dynamicFrame.setLocationRelativeTo(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nemolovich Minecraft RCON Application");
        setMinimumSize(new java.awt.Dimension(650, 475));
        setPreferredSize(new java.awt.Dimension(650, 475));

        outputScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScroll.setAutoscrolls(true);
        outputScroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        output.setEditable(false);
        output.setBackground(new java.awt.Color(51, 51, 51));
        output.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        output.setFont(MIRIAM_FONT_TINY);
        output.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        outputScroll.setViewportView(output);

        commandField.setFont(MIRIAM_FONT_SMALL);
        commandField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
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

        copyButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        clearButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        quitButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        playersButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        saveButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        stopButton.setFont(MIRIAM_FONT_NORMAL_BOLD);
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

        informationPanel.setMaximumSize(new java.awt.Dimension(513, 27));
        informationPanel.setMinimumSize(new java.awt.Dimension(513, 27));

        informationLabel.setFont(MIRIAM_FONT_SMALL);
        informationLabel.setText("Connection to host:");
        informationLabel.setMaximumSize(new java.awt.Dimension(133, 19));
        informationLabel.setMinimumSize(new java.awt.Dimension(133, 19));
        informationLabel.setPreferredSize(new java.awt.Dimension(133, 19));

        informationHostLabel.setFont(MIRIAM_FONT_SMALL_BOLD);
        informationHostLabel.setText("<host>");
        informationHostLabel.setMaximumSize(new java.awt.Dimension(50, 19));
        informationHostLabel.setMinimumSize(new java.awt.Dimension(50, 19));
        informationHostLabel.setPreferredSize(new java.awt.Dimension(50, 19));

        informationProgress.setFont(MIRIAM_FONT_SMALL);
        informationProgress.setForeground(new java.awt.Color(51, 51, 51));
        informationProgress.setIndeterminate(true);
        informationProgress.setMaximumSize(new java.awt.Dimension(100, 19));
        informationProgress.setMinimumSize(new java.awt.Dimension(100, 19));
        informationProgress.setPreferredSize(new java.awt.Dimension(100, 19));
        informationProgress.setString("Pending...");
        informationProgress.setStringPainted(true);
        UIManager.put("ProgressBar.selectionForeground", Color.decode("#333333"));
        UIManager.put("ProgressBar.selectionBackground", Color.decode("#FFFFFF"));

        connectionStatus.setFont(MIRIAM_FONT_SMALL_BOLD);
        connectionStatus.setForeground(new java.awt.Color(153, 0, 0));
        connectionStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        connectionStatus.setText("Disconnected");
        connectionStatus.setMaximumSize(new java.awt.Dimension(100, 19));
        connectionStatus.setMinimumSize(new java.awt.Dimension(100, 19));
        connectionStatus.setPreferredSize(new java.awt.Dimension(100, 19));
        connectionStatus.setVisible(false);

        javax.swing.GroupLayout informationPanelLayout = new javax.swing.GroupLayout(informationPanel);
        informationPanel.setLayout(informationPanelLayout);
        informationPanelLayout.setHorizontalGroup(
            informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(informationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(informationHostLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(connectionStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(informationProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        informationPanelLayout.setVerticalGroup(
            informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informationPanelLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(informationHostLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(informationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(connectionStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(informationProgress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        fileMenu.setFont(MIRIAM_FONT_NORMAL_BOLD);

        reconnectItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        reconnectItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        reconnectItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/reconnect.png"))); // NOI18N
        reconnectItem.setMnemonic('R');
        reconnectItem.setText("Reconnect");
        reconnectItem.setToolTipText("Try to reconnect to server");
        reconnectItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconnectItemActionPerformed(evt);
            }
        });
        fileMenu.add(reconnectItem);

        disconnectItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        disconnectItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        disconnectItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/disconnect.png"))); // NOI18N
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

        quitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        quitItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        quitItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/quit.png"))); // NOI18N
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
        editMenu.setFont(MIRIAM_FONT_NORMAL_BOLD);

        copyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        copyItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/copy.png"))); // NOI18N
        copyItem.setMnemonic('C');
        copyItem.setText("Copy");
        copyItem.setToolTipText("Copy the output content in your clipboard");
        copyItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyItemActionPerformed(evt);
            }
        });
        editMenu.add(copyItem);

        clearItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        clearItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        clearItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/clear.png"))); // NOI18N
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
        commandMenu.setFont(MIRIAM_FONT_NORMAL_BOLD);

        playersMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/players.png"))); // NOI18N
        playersMenu.setMnemonic('Y');
        playersMenu.setText("Players...");
        playersMenu.setToolTipText("Players actions...");
        playersMenu.setFont(MIRIAM_FONT_SMALL_BOLD);

        playersItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        playersItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        playersItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/plays-list.png"))); // NOI18N
        playersItem.setMnemonic('P');
        playersItem.setText("Players list");
        playersItem.setToolTipText("Display the players list");
        playersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playersItemActionPerformed(evt);
            }
        });
        playersMenu.add(playersItem);

        whiteListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        whiteListItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        whiteListItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/white_list.png"))); // NOI18N
        whiteListItem.setMnemonic('W');
        whiteListItem.setText("White-list");
        whiteListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                whiteListItemActionPerformed(evt);
            }
        });
        playersMenu.add(whiteListItem);

        banListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        banListItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        banListItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/ban_list.png"))); // NOI18N
        banListItem.setMnemonic('B');
        banListItem.setText("Ban-list");
        banListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banListItemActionPerformed(evt);
            }
        });
        playersMenu.add(banListItem);

        commandMenu.add(playersMenu);

        commandsListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        commandsListItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        commandsListItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/commandsList.png"))); // NOI18N
        commandsListItem.setMnemonic('O');
        commandsListItem.setText("Commands list");
        commandsListItem.setToolTipText("Display the available commands");
        commandsListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandsListItemActionPerformed(evt);
            }
        });
        commandMenu.add(commandsListItem);
        commandMenu.add(editSep1);

        saveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        saveItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/world.png"))); // NOI18N
        saveItem.setMnemonic('S');
        saveItem.setText("Save the world");
        saveItem.setToolTipText("Save the server world");
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemActionPerformed(evt);
            }
        });
        commandMenu.add(saveItem);

        stopItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        stopItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        stopItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/stop.png"))); // NOI18N
        stopItem.setMnemonic('T');
        stopItem.setText("Stop the server");
        stopItem.setToolTipText("Send a request to stop the server");
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
        helpMenu.setFont(MIRIAM_FONT_NORMAL_BOLD);

        updatesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        updatesItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        updatesItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/update.png"))); // NOI18N
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

        aboutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.CTRL_MASK));
        aboutItem.setFont(MIRIAM_FONT_SMALL_BOLD);
        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/nemolovich/apps/minecraftrcon/gui/icon/about.png"))); // NOI18N
        aboutItem.setMnemonic('B');
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(commandField)
                    .addComponent(outputScroll, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(informationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(informationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(outputScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playersButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        getAccessibleContext().setAccessibleName("mainFrame");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void banListItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banListItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_banListItemActionPerformed

    private void whiteListItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_whiteListItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_whiteListItemActionPerformed

    private void dynamicFrameClearFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynamicFrameClearFilterButtonActionPerformed
        this.clearCommandTableFilter();
    }//GEN-LAST:event_dynamicFrameClearFilterButtonActionPerformed

    private void dynamicFrameFilterFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dynamicFrameFilterFieldKeyTyped
        this.filterCommandTable();
    }//GEN-LAST:event_dynamicFrameFilterFieldKeyTyped

    private void dynamicFrameFilterFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dynamicFrameFilterFieldKeyReleased
        this.filterCommandTable();
    }//GEN-LAST:event_dynamicFrameFilterFieldKeyReleased

    private void dynamicFrameFilterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynamicFrameFilterFieldActionPerformed
        this.filterCommandTable();
    }//GEN-LAST:event_dynamicFrameFilterFieldActionPerformed

    private void closedynamicFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closedynamicFrameActionPerformed
        this.dynamicFrame.setVisible(false);
    }//GEN-LAST:event_closedynamicFrameActionPerformed

    private void commandsListItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_commandsListItemActionPerformed
        this.updateDynamicFrame(TableModelManager.getCommandsFrame(), false, null);
    }// GEN-LAST:event_commandsListItemActionPerformed

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
        this.updateDynamicFrame(TableModelManager.getPlayersFrame(), true,
                this.updatePlayersListTask);
    }// GEN-LAST:event_playersItemActionPerformed

    private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveItemActionPerformed
        this.saveButtonActionPerformed(null);
    }// GEN-LAST:event_saveItemActionPerformed

    private void stopItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stopItemActionPerformed
        this.stopButtonActionPerformed(null);
    }// GEN-LAST:event_stopItemActionPerformed

    private void updatesItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updatesItemActionPerformed
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Launcher.checkForUpdates(args);
                return null;
            }
        }.execute();
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
            this.parrallelInfo("%s", this.playersListCommand);
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
            this.parrallelInfo("%s", "save-all");
        } catch (IOException ex) {
            String error = "Can not save the world";
            LOGGER.error(error, ex);
            this.error(String.format("%s%n", error));
        }
    }// GEN-LAST:event_saveButtonActionPerformed

    private void commandFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_commandFieldActionPerformed
        String command = this.commandField.getText();
        if (!command.isEmpty()) {
            String[] params = command.split(" ");
            String commandName = params[0];
            params = Arrays.copyOfRange(params, 1, params.length);
            this.info(String.format("%s%n", command));
            this.commandField.setText("");
            if (!command.isEmpty()) {
                if (CommandsUtils.getInternalCommands().contains(commandName)) {
                    Command c = CommandsUtils.getInternalCommand(commandName);
                    c.doCommand(params);
                } else if (commandName.equals(String.format("%s",
                        CommandConstants.HELP_COMMAND))
                        && params.length > 0
                        && CommandsUtils.getInternalCommands().contains(
                        String.format("/%s", params[0]))) {
                    this.info(String.format("%s%n", CommandsUtils
                            .getInternalCommandHelp(String.format("/%s", params[0]))));
                } else {
                    try {
                        this.parrallelInfo("%s", command.substring(1));
                    } catch (IOException ex) {
                        String errorMessage = String.format(
                                "Communication error: %s%n", ex.getMessage());
                        this.error(errorMessage);
                        LOGGER.error(errorMessage, ex);
                    }
                }
                if (this.commandHistory.isEmpty()
                        || !this.commandHistory.get(this.commandHistory.size() - 1)
                        .equalsIgnoreCase(command)) {
                    this.commandHistory.add(command);
                }
                this.currentHistoryIndex = this.commandHistory.size();
            }
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
        List<String> suggestions = new ArrayList<>();

        List<String> availableCommands = CommandsUtils.getAvailableCommands();
        for (String cmd : availableCommands) {
            if (cmd.startsWith(line)) {
                suggestions.add(String.format("%s ", cmd));
            } else if (line.startsWith(String.format("/%s ",
                    CommandConstants.HELP_COMMAND))) {
                String hLine = line.substring(String.format("/%s ",
                        CommandConstants.HELP_COMMAND).length());
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
                this.commandField.setText(String.format("/%s %s ",
                        CommandConstants.HELP_COMMAND, text));
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
        if (socket != null) {
            try {
                int requestId = socket.sendRequest(request);
                result = socket.readResponse(requestId);
            } catch (IOException se) {
                if (se instanceof SocketException) {
                    LOGGER.error("Socket error", se);
                    catchException(se);
                    error(String.format("Connection lost%n"));
                    setDisconnected();
                }
                throw se;
            }
        } else {
            throw new SocketException("The socket is null");
        }
        return result;
    }

    private void selectHelpRow(String command) {
        dynamicFrameHelpPane.setText("");
        String help = CommandsUtils.getCommandHelp(command);
        if (help == null || help.isEmpty()) {
            help = this.getHelp(command);
            if (help != null) {
                Matcher multiPage = SERVER_COMMAND_PAGES_PATTERN.matcher(
                        parseColorString(help.replaceAll("\\n", "\\\\n")));
                if (multiPage.matches()) {
                    StringBuilder tmp = new StringBuilder(help);
                    int nbPages = Integer.valueOf(
                            multiPage.group("nbPages"));
                    if (nbPages > 1) {
                        for (int i = 2; i <= nbPages; i++) {
                            tmp.append(getNextHelp(i,
                                    String.format("%s ", command)));
                        }
                    }
                    help = tmp.toString();
                }
                CommandsUtils.addCommandHelp(command, help);
            }
        }
        this.write(help, Level.INFO, dynamicFrameHelpPane);
        this.dynamicFrameHelpPane.setCaretPosition(0);
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
        if (this.socket != null) {
            this.socket.close();
        }
        this.exit(true);
    }

    private void clearConsole() {
        this.output.setText("");
    }

    private void clear() {
        CommandsUtils.clearAll();
        this.clearCommandFrame();
    }

    private void clearCommandFrame() {
        this.dynamicFrameList = TableModelManager.getCommandsFrame().getTable();
        ((CustomTableModel) dynamicFrameList.getModel()).clear();
        this.dynamicFrameList.clearSelection();
        this.dynamicFrameHelpPane.setText("");
        this.dynamicFrameFilterField.setText("");
        this.filterCommandTable();
    }

    private void updateDynamicFrame(TableFrameModel frameModel,
            boolean clear, ParallelTask task) {
        if (!this.dynamicFrameList.equals(frameModel.getTable())) {
            this.dynamicFrameList.clearSelection();

            if (this.dynamicFrameList.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) this.dynamicFrameList.getParent();
                Rectangle rect = this.dynamicFrameList.getCellRect(0, 0, true);
                Point pt = viewport.getViewPosition();
                rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                this.dynamicFrameList.scrollRectToVisible(rect);
            }
            this.dynamicFrameHelpPane.setText("");
            this.dynamicFrameFilterField.setText("");

            this.dynamicFrame.setTitle(frameModel.getFrameTitle());
            this.dynamicFrameFilterField.setToolTipText(
                    frameModel.getFrameFilterTooltip());
            this.dynamicFrameHeader.setText(frameModel.getFrameHeaderLabel());
            this.dynamicFrameHelpLabel.setText(frameModel.getFrameBoxLabel());

            this.dynamicFrameList = frameModel.getTable();

            JButton buttons[] = frameModel.getButtonsList();

            this.dynamicFrameButtonPanel.removeAll();
            if (buttons[0] != null) {
                this.dynamicFrameButtonPanel.setLayout(new FlowLayout(
                        FlowLayout.LEADING, 10, 0));
                this.dynamicFrameButton1 = buttons[0];
                this.dynamicFrameButton1.setBounds(new Rectangle(
                        this.dynamicFrameButton1.getPreferredSize()));
                this.dynamicFrameButton2 = buttons[1];
                this.dynamicFrameButton2.setBounds(new Rectangle(
                        this.dynamicFrameButton2.getPreferredSize()));
                this.dynamicFrameButton3 = buttons[2];
                this.dynamicFrameButton3.setBounds(new Rectangle(
                        this.dynamicFrameButton3.getPreferredSize()));
                this.dynamicFrameButtonPanel.add(this.dynamicFrameButton1);
                this.dynamicFrameButtonPanel.add(this.dynamicFrameButton2);
                this.dynamicFrameButtonPanel.add(this.dynamicFrameButton3);
                this.dynamicFrameButton1.setVisible(true);
                this.dynamicFrameButton2.setVisible(true);
                this.dynamicFrameButton3.setVisible(true);
            } else {
                this.dynamicFrameButton1 = null;
                this.dynamicFrameButton2 = null;
                this.dynamicFrameButton3 = null;
            }
            this.dynamicFrameButtonPanel.validate();
            this.dynamicFrameButtonPanel.repaint();

            if (clear) {
                ((CustomTableModel) this.dynamicFrameList.getModel()).clear();
            }
            if (task != null) {
                task.execute();
            }

            this.dynamicFrame.pack();
            this.dynamicFrameListScroll.setViewportView(this.dynamicFrameList);
        }
        this.dynamicFrameFilterField.requestFocusInWindow();
        this.dynamicFrame.setVisible(true);
    }

    private void filterCommandTable() {
        if (this.dynamicFrameStatusLabel.getText().startsWith(
                String.format(WRONG_REGEX_PATTERN, ""))) {
            this.dynamicFrameStatusLabel.setText("");
            this.dynamicFrameStatusLabel.setToolTipText("");
            this.dynamicFrameStatusLabel.setForeground(this.fieldColor);
        }

        this.dynamicFrameFilterField.setBorder(this.fieldBorder);
        this.dynamicFrameFilterField.setForeground(this.fieldColor);
        try {
            ((CustomTableModel) this.dynamicFrameList.getModel()).filter(
                    this.dynamicFrameFilterField.getText());
        } catch (PatternSyntaxException ex) {
            LOGGER.error(String.format(WRONG_REGEX_PATTERN,
                    ex.getMessage()));
            this.dynamicFrameStatusLabel.setText(String.format(
                    WRONG_REGEX_PATTERN, ex.getMessage()));
            this.dynamicFrameStatusLabel.setToolTipText(String.format(
                    WRONG_REGEX_PATTERN, ex.getMessage()));
            this.dynamicFrameStatusLabel.setForeground(Color.decode("#CC0000"));
            this.dynamicFrameFilterField.setBorder(
                    javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED,
                    Color.decode("#CC9999"), Color.decode("#CC0000")));
            this.dynamicFrameFilterField.setForeground(Color.decode("#CC0000"));
        }
    }

    private void clearCommandTableFilter() {
        if (this.dynamicFrameStatusLabel.getText().startsWith(
                String.format(WRONG_REGEX_PATTERN, ""))) {
            this.dynamicFrameStatusLabel.setText("");
            this.dynamicFrameStatusLabel.setToolTipText("");
            this.dynamicFrameStatusLabel.setForeground(this.fieldColor);
        }
        this.dynamicFrameFilterField.setText("");
        this.dynamicFrameFilterField.setBorder(this.fieldBorder);
        this.dynamicFrameFilterField.setForeground(this.fieldColor);
        ((CustomTableModel) this.dynamicFrameList.getModel()).filter("");
        this.dynamicFrameList.scrollRowToVisible(this.dynamicFrameList.getSelectedRow());
    }

    private boolean quitAskAction() {
        return JOptionPane.showConfirmDialog(this,
                "Do you really want to leave?", "Leave client? oO?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void disconnectAction() {
        socket.close();
        setDisconnected();
        this.warning(String.format("Disconnected from server%n"));
    }

    private void reconnectAction() {
        LOGGER.info("Trying to reconnect to the socket");
        try {
            this.attemptConnect();
        } catch (ConnectionException | AuthenticationException e) {
            this.error(String.format("Can not reconnect to server%n"));
        }
    }

    public void setDisconnectedStatus() {
        this.informationProgress.setVisible(false);
        this.connectionStatus.setVisible(true);
        this.connectionStatus.setText("Disconnected");
        this.connectionStatus.setForeground(Color.decode(
                MinecraftColorsConstants.DARK_RED_COLOR_FG));
    }

    public void setConnectedStatus() {
        this.informationProgress.setVisible(false);
        this.connectionStatus.setVisible(true);
        this.connectionStatus.setText("Connected");
        this.connectionStatus.setForeground(Color.decode(
                MinecraftColorsConstants.DARK_GREEN_COLOR_FG));
    }

    public void setInProgress() {
        this.informationProgress.setVisible(true);
        this.connectionStatus.setVisible(false);
    }

    public void setHostText(String host) {
        this.informationHostLabel.setText(host);
    }

    private void setDisconnected() {
        PingThread.getInstance().disable();
        this.setDisconnectedStatus();
        this.setState(false);
        this.clear();
    }

    private void setConnected() {
        this.setConnectedStatus();
        this.setState(true);
    }

    private void setState(boolean action) {
        if ((this.socket == null || this.socket != null
                && this.socket.isClosed()) ^ action) {
            this.playersButton.setEnabled(action);
            this.playersMenu.setEnabled(action);
            this.playersItem.setEnabled(action);
            this.banListItem.setEnabled(action);
            this.whiteListItem.setEnabled(action);
            this.commandsListItem.setEnabled(action);
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
    private javax.swing.JMenuItem banListItem;
    private javax.swing.JButton clearButton;
    private javax.swing.JMenuItem clearItem;
    private javax.swing.JButton closeAboutFrame;
    private javax.swing.JButton closedynamicFrame;
    private javax.swing.JTextField commandField;
    private javax.swing.JMenu commandMenu;
    private javax.swing.JMenuItem commandsListItem;
    private javax.swing.JLabel connectionStatus;
    private javax.swing.JLabel contactValue;
    private javax.swing.JButton copyButton;
    private javax.swing.JMenuItem copyItem;
    private javax.swing.JMenuItem disconnectItem;
    private javax.swing.JDialog dynamicFrame;
    private javax.swing.JButton dynamicFrameButton1;
    private javax.swing.JButton dynamicFrameButton2;
    private javax.swing.JButton dynamicFrameButton3;
    private javax.swing.JPanel dynamicFrameButtonPanel;
    private javax.swing.JButton dynamicFrameClearFilterButton;
    private javax.swing.JTextField dynamicFrameFilterField;
    private javax.swing.JLabel dynamicFrameFilterLabel;
    private javax.swing.JLabel dynamicFrameHeader;
    private javax.swing.JLabel dynamicFrameHelpLabel;
    private javax.swing.JTextPane dynamicFrameHelpPane;
    private javax.swing.JScrollPane dynamicFrameHelpScroll;
    private org.jdesktop.swingx.JXTable dynamicFrameList;
    private javax.swing.JScrollPane dynamicFrameListScroll;
    private javax.swing.JLabel dynamicFrameStatusLabel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel headerImage;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel informationHostLabel;
    private javax.swing.JLabel informationLabel;
    private javax.swing.JPanel informationPanel;
    private javax.swing.JProgressBar informationProgress;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextPane output;
    private javax.swing.JScrollPane outputScroll;
    private javax.swing.JButton playersButton;
    private javax.swing.JMenuItem playersItem;
    private javax.swing.JMenu playersMenu;
    private javax.swing.JButton quitButton;
    private javax.swing.JMenuItem quitItem;
    private javax.swing.JMenuItem reconnectItem;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveItem;
    private javax.swing.JLabel sourcesValue;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenuItem stopItem;
    private javax.swing.JMenuItem updatesItem;
    private javax.swing.JLabel versionValue;
    private javax.swing.JMenuItem whiteListItem;
    // End of variables declaration//GEN-END:variables

    public void catchException(Exception ex) {
        LOGGER.error("Error: " + ex);
        JXErrorPane.showDialog(
                this, new ErrorInfo("Error", "Error occured", null, "Error", ex,
                java.util.logging.Level.SEVERE, null));
    }

    private void fine(final String msg) {
        this.fine(msg, this.output);
    }

    private void fine(final String msg, final JTextPane output) {
        this.write(msg, Level.FINE, output);
    }

    private void info(final String msg) {
        this.info(msg, this.output);
    }

    private void info(final String msg, final JTextPane output) {
        this.write(msg, Level.INFO, output);
    }

    private void parrallelInfo(final String format, final String request)
            throws IOException {
        this.parrallelInfo(format, request, this.output);
    }

    private void parrallelInfo(final String format, final String request,
            final JTextPane output) throws IOException {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    String result = getRequestResponse(request);
                    info(String.format(format, result), output);
                } catch (IOException ex) {
                    throw ex;
                }
                return null;
            }
        }.execute();
    }

    private void warning(final String msg) {
        this.warning(msg, this.output);
    }

    private void warning(final String msg, final JTextPane output) {
        this.write(msg, Level.WARNING, output);
    }

    private void error(final String msg) {
        this.error(msg, this.output);
    }

    private void error(final String msg, final JTextPane output) {
        this.write(msg, Level.ERROR, output);
    }

    private synchronized void write(final String message, final Level level,
            final JTextPane output) {

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
            if (style == DEFAULT_STYLE && !message.startsWith("/")) {
                String[] parts = message
                        .split(MinecraftColorsConstants.MINECRAFT_COLOR_PREFIX);
                if (!message.startsWith(MinecraftColorsConstants.MINECRAFT_COLOR_PREFIX)
                        && parts.length > 0) {
                    parts[0] = String.format("%s%s",
                            MinecraftColorsConstants.WHITE_COLOR_CODE, parts[0]);
                }
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
                        output.getDocument().insertString(
                                output.getDocument().getLength(),
                                part.substring(1), colorStyle);
                    }
                }
                if (!message.replaceAll("\\r", "").endsWith("\n")) {
                    output.getDocument().insertString(
                            output.getDocument().getLength(), String.format("%n"),
                            style);
                }
            } else {
                output.getDocument().insertString(
                        output.getDocument().getLength(), message, style);
            }
            output.setCaretPosition(output.getDocument().getLength());
        } catch (BadLocationException ex) {
            LOGGER.error("GUI Log error", ex);
        }

    }
}
