package fr.nemolovich.apps.minecraftrcon;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.gui.MainFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public class Connector {

    private static final Pattern HOST_PATTERN = Pattern.compile("^(?<host>("
        + "(\\d{1,3}\\.){3}\\d{1,3})|([A-Za-z0-9.-]+))"
        + "\\:(?<port>\\d+)$");

    /**
     * @param args
     * @throws AuthenticationException
     */
    public static void main(String[] args) throws AuthenticationException {
        BasicConfigurator.configure();

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

        hostField.setText("nemolovich.dynamic-dns.net:20066");
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
                } catch (AuthenticationException e) {
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
}
