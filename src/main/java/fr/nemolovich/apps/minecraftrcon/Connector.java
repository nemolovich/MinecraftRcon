package fr.nemolovich.apps.minecraftrcon;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import org.apache.log4j.BasicConfigurator;

public class Connector {

    /**
     * @param args
     * @throws AuthenticationException
     */
    public static void main(String[] args) throws AuthenticationException {
        BasicConfigurator.configure();

        ClientSocket socket = new ClientSocket("192.168.1.69", 20066,
            "Minecraft2580");

        int requestId = socket.sendRequest("/help");
        String response = socket.readResponse(requestId);
        System.out.println(response);

        socket.close();
    }
}
