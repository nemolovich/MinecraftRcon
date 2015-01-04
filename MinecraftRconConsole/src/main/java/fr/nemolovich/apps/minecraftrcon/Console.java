package fr.nemolovich.apps.minecraftrcon;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.socket.ClientSocket;
import fr.nemolovich.apps.minecraftrcon.utils.StringUtils;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;

public class Console {

    private static final String USAGE = String.format(
        "Parameters: <COMMAND> [,ARGS]");
    private static final String DEFAULT_CONFIG_FILE = "connection.cfg";

    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();

        String configFile = DEFAULT_CONFIG_FILE;
        String sysConf = System.getProperty("connectionFile");
        if (sysConf != null) {
            configFile = sysConf;
        }

        Properties prop = new Properties();

        try {
            prop.load(new FileReader(configFile));
        } catch (IOException ex) {
            System.err.println("Can not load config file ");
            return;
        }

        int port = 25575;

        if (args.length < 1) {
            System.err.println(USAGE);
            return;
        }

        List<String> params = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--debug")
                || args[i].equalsIgnoreCase("--java-home")
                || args[i].equalsIgnoreCase("--connection-file")) {
                i++;
            } else {
                params.add(args[i]);
            }
        }

        String host = prop.getProperty("host");
        String password = prop.getProperty("password");
        if (host == null || password == null) {
            System.err.println(USAGE);
            return;
        }

        String portValue = prop.getProperty("port");
        if (portValue != null) {
            try {
                port = Integer.valueOf(portValue);
            } catch (NumberFormatException nfe) {
                System.err.println("Port format is invalid: " + nfe.getMessage());
            }
        }

        try {
            ClientSocket socket = new ClientSocket(host, port, password);
            String command = StringUtils.join((String[]) params.toArray(
                new String[0]));
            int reqID = socket.sendRequest(command);
            String result = socket.readResponse(reqID);
            System.out.printf("Response:%n%s%n", StringUtils.parseColorString(
                result));
            socket.close();
        } catch (ConnectionException ex) {
            System.err.println("Connection error: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Communication error: " + ex.getMessage());
        } catch (AuthenticationException ex) {
            System.err.println("The connection failed: "
                + ex.getLocalizedMessage());
        }
    }
}
