/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui;

import fr.nemolovich.apps.minecraftrcon.ClientSocket;
import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 *
 * @author Nemolovich
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * UID
     */
    private static final long serialVersionUID = 2901406508413961272L;

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class
        .getName());

    private static final String FRAME_ICON = "icon/icon.png";
    private static final String NB_RESOURCES_PATH = "/fr/nemolovich/apps/homeapp/admin/gui/";

    private static final Pattern HOST_PATTERN = Pattern.compile("^(?<host>("
        + "(\\d{1,3}\\.){3}\\d{1,3})|([A-Za-z0-9.-]+))"
        + "\\:(?<port>\\d+)$");

    private static final Pattern SERVER_COMMAND_PATTERN = Pattern
        .compile("(?<cmd>\\w+)");

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

        setIconImage(Toolkit.getDefaultToolkit().getImage(
            MainFrame.class.getResource(NB_RESOURCES_PATH
                .concat(FRAME_ICON))));
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
            public Component getComponentAfter(Container aContainer, Component aComponent) {

                Component result = null;
                if (aComponent.equals(output)) {
                    result = copyButton;
                } else if (aComponent.equals(commandField)) {
                    result = copyButton;
                } else if (aComponent.equals(copyButton)) {
                    result = clearButton;
                } else if (aComponent.equals(clearButton)) {
                    result = quitButton;
                } else if (aComponent.equals(quitButton)) {
                    result = output;
                }
                return result;
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                Component result = null;
                if (aComponent.equals(output)) {
                    result = quitButton;
                } else if (aComponent.equals(commandField)) {
                    result = output;
                } else if (aComponent.equals(copyButton)) {
                    result = output;
                } else if (aComponent.equals(clearButton)) {
                    result = copyButton;
                } else if (aComponent.equals(quitButton)) {
                    result = clearButton;
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
//        try {
//            this.socket.setLogger((ISocketLogger) this.output);
//            this.socket.connect();
//        } catch (Exception ex) {
//            ((ISocketLogger) this.output).error(String.format(
//                "Connection failed. %s%n", ex.getMessage()));
//            LOGGER.log(Level.SEVERE, "Can not connect to socket", ex);
//            exit();
//            return;
//        }
//        try {
//            ((ISocketLogger) this.output).info(String.format("%s%n",
//                this.socket.readResponse()));
//        } catch (IOException ex) {
//            ((ISocketLogger) this.output).warning(String.format(
//                "Can not read welcome message: %s%n", ex.getMessage()));
//            LOGGER.log(Level.SEVERE, "Can not read welcome message", ex);
//            exit();
//        }
//        try {
//            this.socket.sendRequest(String.format("%s%s",
//                CommandConstants.COMMAND_START,
//                CommandConstants.HELP_COMMAND));
//            String helpMsg = this.socket.readResponse();
//            List<String> serverCommands = parseServerCommands(helpMsg);;
//            CommandsUtils.addServerCommand(serverCommands);
//        } catch (IOException ex) {
//            ((ISocketLogger) this.output).warning(String.format(
//                "Can not retrive server commands: %s%n", ex.getMessage()));
//            LOGGER.log(Level.SEVERE, "Can not retrive server commands", ex);
//        }

    }

    private static List<String> parseServerCommands(String msg) {
        List<String> commands = new ArrayList();
        Matcher matcher = SERVER_COMMAND_PATTERN.matcher(msg);

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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outputScroll = new javax.swing.JScrollPane();
        output = new JTextPane();
        commandField = new javax.swing.JTextField();
        copyButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Home Application Administration");
        setMinimumSize(new java.awt.Dimension(600, 400));
        setPreferredSize(new java.awt.Dimension(600, 400));

        outputScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScroll.setAutoscrolls(true);
        outputScroll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        output.setEditable(false);
        output.setBackground(new java.awt.Color(204, 204, 204));
        output.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        output.setFont(new java.awt.Font("Miriam Fixed", 0, 11)); // NOI18N
        output.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        outputScroll.setViewportView(output);

        commandField.setFont(new java.awt.Font("Miriam Fixed", 0, 12)); // NOI18N
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

        copyButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
        copyButton.setText("copy");
        copyButton.setMaximumSize(new java.awt.Dimension(79, 26));
        copyButton.setMinimumSize(new java.awt.Dimension(79, 26));
        copyButton.setPreferredSize(new java.awt.Dimension(79, 26));
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        clearButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
        clearButton.setText("clear");
        clearButton.setMaximumSize(new java.awt.Dimension(79, 26));
        clearButton.setMinimumSize(new java.awt.Dimension(79, 26));
        clearButton.setPreferredSize(new java.awt.Dimension(79, 26));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        quitButton.setFont(new java.awt.Font("Miriam Fixed", 1, 14)); // NOI18N
        quitButton.setText("Quit");
        quitButton.setMaximumSize(new java.awt.Dimension(79, 26));
        quitButton.setMinimumSize(new java.awt.Dimension(79, 26));
        quitButton.setPreferredSize(new java.awt.Dimension(79, 26));
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(commandField)
                    .addComponent(outputScroll, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(outputScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
            try {
//                this.socket.sendRequest(ClientSocket.getQuitCommand());
//                ((ISocketLogger) this.output).info(String.format("%s%n",
//                    ClientSocket.getQuitCommand()));
//                ((ISocketLogger) this.output).info(String.format("%s%n",
//                    this.socket.readResponse()));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error while sending quit command", ex);
            }
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

    private void clearConsole() {
        this.output.setText("");
    }

    private void commandFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_commandFieldActionPerformed
        String command = this.commandField.getText();
        String[] args = command.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);
//        ((ISocketLogger) this.output).info(String.format("%s%n",
//            command));
        this.commandField.setText("");
//        if (!command.isEmpty()) {
//            if (CommandsUtils.getInternalCommands().contains(
//                commandName)) {
//                Command c = CommandsUtils.getInternalCommand(
//                    commandName);
//                c.doCommand(args);
//            } else if (commandName.equals(CommandConstants.HELP_COMMAND)
//                && args.length > 0
//                && CommandsUtils.getInternalCommands().contains(args[0])) {
//                ((ISocketLogger) this.output).info(String.format("%s%n",
//                    CommandsUtils.getInternalCommandHelp(args[0])));
//            } else {
//                try {
//                    this.socket.sendRequest(command);
//                    ((ISocketLogger) this.output).info(String.format("%s%n",
//                        this.socket.readResponse()));
//                    if (command.startsWith(ClientSocket.getQuitCommand())) {
//                        new SwingWorker() {
//
//                            @Override
//                            protected Object doInBackground() throws Exception {
//                                ((ISocketLogger) output)
//                                    .error(String
//                                        .format("Closing application in 3 secondes%n"));
//                                Thread.sleep(3000);
//                                return null;
//                            }
//
//                            @Override
//                            protected void done() {
//                                exit(true);
//                            }
//                        }.execute();
//                    }
//                } catch (IOException ex) {
//                    ((ISocketLogger) this.output).error(String.format(
//                        "Communication error: %s%n", ex.getMessage()));
//                    LOGGER.log(Level.SEVERE, null, ex);
//                }
//            }
            this.commandHistory.add(command);
            this.currentHistoryIndex = this.commandHistory.size();
//        }
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
//        if (line.length() > 0 && line.charAt(0) == CommandConstants.COMMAND_START) {
            line = line.substring(1);
            List<String> suggestions = new ArrayList();
//            for (String cmd : CommandsUtils.getAvailableCommands()) {
//                if (cmd.startsWith(line)) {
//                    suggestions.add(String.format("%s%s ",
//                        CommandConstants.COMMAND_START, cmd));
//                }
//            }
            if (suggestions.size() == 1) {
                this.commandField.setText(suggestions.get(0));
//            } else if (suggestions.size() > 1 && suggestions.size()
//                < CommandsUtils.getAvailableCommands().size()) {
//                StringBuilder display = new StringBuilder();
//                display.append(String.format("Available commmands:%n"));
//                for (String cmd : suggestions) {
//                    display.append(String.format("\t%s%n", cmd));
//                }
//                ((SocketLoggerArea) this.output).info(display.toString());
            }
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static final void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">
		/*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase
         * /tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                .getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
                java.util.logging.Level.SEVERE, null, ex);
        }
		// </editor-fold>

        // </editor-fold>
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        JPanel header = new JPanel(new GridLayout(1, 0, 2, 2));
        final String defaultText = "Please enter connection informations";

        final JLabel headerLabel = new JLabel(defaultText,
            SwingConstants.CENTER);

        final Font defaultFont = headerLabel.getFont();
        Font boldFont = new Font(defaultFont.getName(), Font.BOLD,
            defaultFont.getSize());
        final Color defaultColor = headerLabel.getForeground();
        header.add(headerLabel);
        panel.add(header, BorderLayout.NORTH);

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Host", SwingConstants.RIGHT));
        label.add(new JLabel("Password", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField hostField = new JTextField("localhost:8181");
        controls.add(hostField);
        hostField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                headerLabel.setText(defaultText);
                headerLabel.setFont(defaultFont);
                headerLabel.setForeground(defaultColor);
            }
        });
        JPasswordField passwordField = new JPasswordField();
        controls.add(passwordField);
        panel.add(controls, BorderLayout.CENTER);

        String host;

        Matcher matcher = null;

        while (matcher == null || !matcher.matches()) {
            if (JOptionPane.showConfirmDialog(null, panel, "Host informations",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                host = hostField.getText();
                matcher = HOST_PATTERN.matcher(host);
                if (!matcher.matches()) {
                    headerLabel.setForeground(Color.RED);
                    headerLabel.setFont(boldFont);
                    headerLabel.setText("Host format: <HOST>:<PORT>");
                }
            } else {
                return;
            }
        }

        final String hostName = matcher.group("host");
        final int hostPort = Integer.parseInt(matcher.group("port"));
        final String password = new String(passwordField.getPassword());

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
					new MainFrame(new ClientSocket(hostName, hostPort, password))
					    .setVisible(true);
				} catch (AuthenticationException e) {
					// XXX: Auth failed
					e.printStackTrace();
				}
            }
        });
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
    private javax.swing.JButton quitButton;
    // End of variables declaration//GEN-END:variables
}
