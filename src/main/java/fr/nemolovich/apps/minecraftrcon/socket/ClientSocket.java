/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.socket;

import fr.nemolovich.apps.minecraftrcon.exceptions.AuthenticationException;
import fr.nemolovich.apps.minecraftrcon.exceptions.ConnectionException;
import fr.nemolovich.apps.minecraftrcon.packet.Packet;
import fr.nemolovich.apps.minecraftrcon.packet.PacketConstants;
import fr.nemolovich.apps.minecraftrcon.packet.PacketType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Nemolovich
 */
public class ClientSocket {

    private static final Logger LOGGER = Logger.getLogger(ClientSocket.class);

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public ClientSocket(String host, int port, String password)
        throws ConnectionException, AuthenticationException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.socket = new Socket(host, port);
        } catch (IOException ex) {
            throw new ConnectionException(host, port, ex);
        }
        LOGGER.info(String.format("Connected to '%s'", host));
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(this.socket.getInputStream());
            dos = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException ex) {
            LOGGER.error(String.format("Can not open stream from connection"),
                ex);
        }
        this.input = dis;
        this.output = dos;
        if (this.input != null && this.output != null) {
            if (this.requestForConnection(password)) {
                LOGGER.info("Authentication succeed!");
            }
        }
    }

    public int sendRequest(String request) {
        Packet packet = new Packet(PacketType.SERVERDATA_EXECCOMMAND, request);
        byte[] requestBytes = packet.getBytes();
        try {
            synchronized (this.output) {
                this.output.write(requestBytes);
            }
        } catch (IOException ex) {
            LOGGER.error("Can not send request to server", ex);
        }
        return packet.getId();
    }

    public String readResponse(int requestId) throws IOException {
        String response = null;
        byte[] buffer = new byte[PacketConstants.MAX_BUFFER_SIZE];
        int length = 0;

        synchronized (this.socket) {

            while (this.input.available() > 0 || length < 10) {
                byte b = this.input.readByte();
                buffer[length++] = b;
            }

            byte[] size = Arrays.copyOfRange(buffer,
                PacketConstants.SIZE_BLOCKS_INDEX,
                PacketConstants.SIZE_BLOCKS_INDEX
                + PacketConstants.SIZE_BLOCKS_SIZE);
            byte[] id = Arrays.copyOfRange(buffer, PacketConstants.ID_BLOCKS_INDEX,
                PacketConstants.ID_BLOCKS_INDEX
                + PacketConstants.ID_BLOCKS_SIZE);
            byte[] type = Arrays.copyOfRange(buffer,
                PacketConstants.TYPE_BLOCKS_INDEX,
                PacketConstants.TYPE_BLOCKS_INDEX
                + PacketConstants.TYPE_BLOCKS_SIZE);

            int responseSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
            int responseId = ByteBuffer.wrap(id).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
            int responseType = ByteBuffer.wrap(type).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();

            if (responseType == PacketType.SERVERDATA_RESPONSE_VALUE.value()
                && responseId == requestId) {
                int dataLength = responseSize
                    - (PacketConstants.ID_BLOCKS_SIZE
                    + PacketConstants.TYPE_BLOCKS_SIZE + PacketConstants.NULL_BLOCKS_SIZE);
                byte[] data = new byte[dataLength];
                for (int i = 0; i < dataLength; i++) {
                    data[i] = buffer[PacketConstants.DATA_BLOCKS_INDEX + i];
                }
                response = new String(data);
            }
        }
        return response;
    }

    public boolean ping() {
        boolean result = false;
        try {
            int id = this.sendRequest("ping");
            this.readResponse(id);
            result = true;
        } catch (IOException ex) {
            LOGGER.error("Can not retrive ping response", ex);
        }
        return result;
    }

    private boolean requestForConnection(String password)
        throws AuthenticationException {
        boolean result = false;

        Packet packet = new Packet(PacketType.SERVERDATA_AUTH, password);
        byte[] request = packet.getBytes();
        synchronized (this.socket) {
            try {
                this.output.write(request);
            } catch (IOException ex) {
                LOGGER.error("Can not send request to server", ex);
            }

            byte[] buffer = new byte[PacketConstants.MAX_BUFFER_SIZE];
            int length = 0;

            try {
                while (this.input.available() > 0 || length < 10) {
                    byte b = this.input.readByte();
                    buffer[length++] = b;
                }
            } catch (IOException ex) {
                LOGGER.error("Error while reading response", ex);
            }

            byte[] id = Arrays.copyOfRange(buffer, PacketConstants.ID_BLOCKS_INDEX,
                PacketConstants.ID_BLOCKS_INDEX
                + PacketConstants.ID_BLOCKS_SIZE);
            byte[] type = Arrays.copyOfRange(buffer,
                PacketConstants.TYPE_BLOCKS_INDEX,
                PacketConstants.TYPE_BLOCKS_INDEX
                + PacketConstants.TYPE_BLOCKS_SIZE);

            int responseId = ByteBuffer.wrap(id).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
            int responseType = ByteBuffer.wrap(type).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();

            if (responseType == PacketType.SERVERDATA_AUTH_RESPONSE.value()
                && responseId == packet.getId()) {
                result = true;
            } else {
                this.close();
                throw new AuthenticationException();
            }
        }

        return result;
    }

    public void close() {
        synchronized (this.socket) {
            if (this.socket != null) {
                try {
                    this.socket.close();
                    LOGGER.info("Socket closed");
                } catch (IOException ex) {
                    LOGGER.error("Can not close socket", ex);
                }
            }
        }
    }

    public boolean isClosed() {
        synchronized (this.socket) {
            return this.socket == null || this.socket.isClosed();
        }
    }

}
