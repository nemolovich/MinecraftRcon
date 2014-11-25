package fr.nemolovich.apps.minecraftrcon;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.gui.MainFrame;
import fr.nemolovich.apps.minecraftrcon.update.Updater;
import fr.nemolovich.apps.minecraftrcon.update.WindowsUpdater;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public class Launcher {

    private static final Pattern HOST_PATTERN = Pattern.compile("^(?<host>("
        + "(\\d{1,3}\\.){3}\\d{1,3})|([A-Za-z0-9.-]+))"
        + "\\:(?<port>\\d+)$");
    private static final Logger LOGGER = Logger.getLogger(Launcher.class);

    private static final String REMOTE_DOWNLOAD
        = "https://rawgit.com/nemolovich/MinecraftRcon/master/downloads/";

    private static final String APP_NAME = "MinecraftRcon";

    /**
     * @param args
     * @throws AuthenticationException
     */
    public static void main(String[] args) throws AuthenticationException {
        BasicConfigurator.configure();

//        if (true) {
//            try {
//                doNotUse("MinecraftRcon.db", "jar:file:/C:/Users/Nemolovich/Desktop/MinecraftRCON/MinecraftRcon.jar!/");
//                return;
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
//                return;
//            }
//        }
        if (args.length > 0) {

            for (String arg : args) {
                if (arg.equalsIgnoreCase("--update")
                    || arg.equalsIgnoreCase("-u")) {
                    String[] dst = new String[args.length];
                    System.arraycopy(args, 0, dst, 0, args.length);
                    checkForUpdates(dst);
                    return;
                } else if (arg.equalsIgnoreCase("--version")
                    || arg.equalsIgnoreCase("-v")) {
                    System.out.printf("Nemolovich MinecraftRCON version %s%n", getCurrentVersion());
                }
            }
        }

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
        JTextField hostField = new JTextField("localhost:25575");
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

        hostField.setText("raspberry:20066");
        passwordField.setText("Minecraft2580");

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
                MainFrame mf = null;
                try {
                    mf = new MainFrame(new ClientSocket(hostName, hostPort,
                        password));
                    mf.setVisible(true);
                } catch (AuthenticationException | ConnectionException e) {
                    if (mf != null) {
                        mf.close();
                    }
                    JXErrorPane.showDialog(
                        mf,
                        new ErrorInfo("Connection error", String.format(
                                "The connection to host '%s' failed",
                                hostName), null, "Error", e, Level.SEVERE,
                            null));
                }
            }
        });
    }

    public static final void doNotUse(String chose, String machin)
        throws IOException {

        File f = new File(chose);

        int thing = (int) new File(machin.substring(machin.lastIndexOf(':') - 1, machin.indexOf('!'))).length();

        URL truc = new URL(machin);

        JarURLConnection chouette = (JarURLConnection) truc.openConnection();

        Attributes bidule = chouette.getMainAttributes();

        try (ObjectOutputStream porte = new ObjectOutputStream(
            new BufferedOutputStream(new FileOutputStream(f)))) {
            porte.writeInt(thing);
            porte.writeChar('|');
            porte.writeChars(bidule
                .getValue(Attributes.Name.IMPLEMENTATION_VERSION));
        }
    }

    private static String getCurrentVersion() {
        String result;

        final URL localURL = Launcher.class
            .getResource("/META-INF/MANIFEST.MF");

        Manifest manifest;
        try {
            manifest = new Manifest(localURL.openStream());

            Attributes attrs = manifest.getMainAttributes();

            result = attrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        } catch (IOException ex) {
            LOGGER.error("Can not find application version", ex);
            result = "Unknown";
        }
        return result;
    }

    private static int compareVersion(String v1, String v2) {
        int result = 0;
        if (v1 != null && v2 != null) {
            String[] v1Values = v1.split("\\.");
            String[] v2Values = v2.split("\\.");
            int majv1;
            int minv1;
            int verv1;
            int majv2;
            int minv2;
            int verv2;
            try {
                if (v1Values.length == 3 && v2Values.length == 3) {
                    majv1 = Integer.valueOf(v1Values[0]);
                    minv1 = Integer.valueOf(v1Values[1]);
                    verv1 = Integer.valueOf(v1Values[2]);
                    majv2 = Integer.valueOf(v2Values[0]);
                    minv2 = Integer.valueOf(v2Values[1]);
                    verv2 = Integer.valueOf(v2Values[2]);
                    if (majv1 > majv2) {
                        result = 1;
                    } else if (majv1 < majv2) {
                        result = -1;
                    } else {
                        if (minv1 > minv2) {
                            result = 1;
                        } else if (minv1 < minv2) {
                            result = -1;
                        } else {
                            if (verv1 > verv2) {
                                result = 1;
                            } else if (verv1 < verv2) {
                                result = -1;
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                LOGGER.warn("Can not compare versions", ex);
            }
        }
        return result;
    }

    private static void checkForUpdates(String... args) {
        try {
            LOGGER.info("Check for updates...");

            String currentVersion = getCurrentVersion();

            LOGGER.info("Current version: " + currentVersion);

            final URL url = new URL(String.format("%s%s.db",
                REMOTE_DOWNLOAD, APP_NAME));

            int fileSize;
            StringBuilder version;
            try (ObjectInputStream is = new ObjectInputStream(url.openStream())) {
                fileSize = is.readInt();
                if (is.readChar() == '|') {
                    version = new StringBuilder();
                    while (is.available() > 0) {
                        version.append(is.readChar());
                    }
                } else {
                    LOGGER.error("Can not retrieve remote information");
                    return;
                }
            }
            String remoteVersion;
            try {
                remoteVersion = version.toString();
            } catch (NumberFormatException e) {
                throw new IOException("Unavailable remote version");
            }
            LOGGER.info(
                "Remote version: " + remoteVersion);
            if (compareVersion(remoteVersion, currentVersion) > 0) {
                if (JOptionPane.showConfirmDialog(null,
                    "<html>A new version of <b>Nemolovich MinecraftRCON</b> is "
                    + "available.<br/>Do you want to download it?</html>",
                    "Updates are available", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    DataOutputStream dlOut = null;
                    DataInputStream dlIn = null;
                    String dlFile = String.format(
                        "%s-%s.jar", APP_NAME,
                        remoteVersion);
                    File download = new File(dlFile);
                    try {

                        LOGGER.info(String.format("Downloading file [%s]...",
                            dlFile));

                        dlOut = new DataOutputStream(
                            new FileOutputStream(download));

                        String dlLink = String.format(
                            "%s%s", REMOTE_DOWNLOAD, dlFile);

                        LOGGER.info(String.format("File size: %d", fileSize));

                        URL dlURL = new URL(dlLink);
                        dlIn = new DataInputStream(
                            dlURL.openStream());

                        byte buffer[] = new byte[512];
                        int dlSize = 0;
                        int len;
                        String total = "";
                        while ((len = dlIn.read(buffer)) != -1) {
                            dlOut.write(buffer, 0, len);
                            dlSize += len;
                            int pct = (int) ((double) (dlSize * 100.) / (double) fileSize);

                            total = "";
                            for (int i = 0; i < pct / 5; i++) {
                                total += "#";
                            }
                            System.out.print(String.format(
                                "Downloading file: [%-20s] %d/%d KB %d%%\r",
                                total, dlSize, fileSize, pct));
                        }
                        System.out.print(String.format(
                            "Downloading file: [%-20s] %d/%d KB %d%%\r",
                            total, fileSize, fileSize, 100));
                    } finally {
                        if (dlIn != null) {
                            dlIn.close();
                        }
                        if (dlOut != null) {
                            dlOut.close();
                        }
                        LOGGER.info(String.format("File %s sucessfully downloaded!",
                            dlFile));
                    }
                    List<String> optArgs = new ArrayList(Arrays.asList(args));
                    optArgs.remove("-u");
                    optArgs.remove("--update");
                    Updater updater = new WindowsUpdater(
                        String.format("%s.jar", APP_NAME), dlFile,
                        optArgs.toArray(new String[0]));
                    try {
                        updater.update();
                    } catch (IOException ioe) {
                        JXErrorPane.showDialog(null,
                            new ErrorInfo("Restarting error",
                                "The application failed to restart", null,
                                "Error", ioe, Level.SEVERE, null));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                    "The software is already up-to-date",
                    "No update found", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException ex) {
            LOGGER.error(
                "Can not retrieve remote version", ex);
        }
    }
}
