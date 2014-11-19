package fr.nemolovich.apps.minecraftrcon;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.gui.MainFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
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

    /**
     * @param args
     * @throws AuthenticationException
     */
    public static void main(String[] args) throws AuthenticationException {
        BasicConfigurator.configure();

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("--update")
                || args[0].equalsIgnoreCase("-u")) {
                checkForUpdates();
                return;
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
                    mf = new MainFrame(new ClientSocket(
                        hostName, hostPort, password));
                    mf.setVisible(true);
                } catch (AuthenticationException | ConnectionException e) {
                    if (mf != null) {
                        mf.close();
                    }
                    JXErrorPane.showDialog(mf,
                        new ErrorInfo("Connection error",
                            String.format("The connection to host '%s' failed",
                                hostName),
                            null, "Error", e, Level.SEVERE, null));
                }
            }
        });
    }

    public static void doNotUse(String chose, String machin) throws IOException {

        File f = new File(chose);

        URL truc = new URL(machin);
        JarURLConnection bidule = (JarURLConnection) truc.openConnection();

        Attributes atts = bidule.getMainAttributes();
        System.out.println(atts.getValue(Attributes.Name.IMPLEMENTATION_VERSION));

        try (ObjectOutputStream porte = new ObjectOutputStream(
            new BufferedOutputStream(new FileOutputStream(f)))) {
            porte.writeChars(atts.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
        }
    }

    private static void checkForUpdates() {
        try {
            InputStream is = new FileInputStream(
                "https://github.com/nemolovich/MinecraftRcon/MinecraftRcon.db");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Launcher.class).error("Can not retrieve remote version");
        }
    }
}
