package fr.nemolovich.apps.minecraftrcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import fr.nemolovich.apps.minecraftrcon.packet.Packet;
import fr.nemolovich.apps.minecraftrcon.packet.PacketType;

public class Connector {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("nemolovich.dynamic-dns.net", 20066);

		DataInputStream is = new DataInputStream(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		Packet packet = new Packet(PacketType.SERVERDATA_AUTH, "Minecraft2580");
		byte[] result = packet.getBytes();
		os.write(result);

		byte[] buffer = new byte[1024 + 14];
		int length = 0;

		while (is.available() > 0 || length < 10) {
			byte b = is.readByte();
			buffer[length++] = b;
		}

		byte[] size = Arrays.copyOfRange(buffer, 0, 4);
		byte[] id = Arrays.copyOfRange(buffer, 4, 8);
		byte[] type = Arrays.copyOfRange(buffer, 8, 12);

		int responseSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();
		int responseId = ByteBuffer.wrap(id).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();
		int responseType = ByteBuffer.wrap(type).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();

		if (responseType == PacketType.SERVERDATA_AUTH_RESPONSE.value()) {
			if (responseId == -1 || responseId != packet.getId()) {
				System.err.println("Authentication exception");
				socket.close();
				System.exit(0);
			}
		}

		System.out.println("OK");

		packet = new Packet(PacketType.SERVERDATA_EXECCOMMAND, "status");
		result = packet.getBytes();
		os.write(result);

		buffer = new byte[1024 + 14];
		length = 0;

		while (is.available() > 0 || length < 10) {
			byte b = is.readByte();
			buffer[length++] = b;
		}

		size = Arrays.copyOfRange(buffer, 0, 4);
		id = Arrays.copyOfRange(buffer, 4, 8);
		type = Arrays.copyOfRange(buffer, 8, 12);

		responseSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();
		responseId = ByteBuffer.wrap(id).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();
		responseType = ByteBuffer.wrap(type).order(ByteOrder.LITTLE_ENDIAN)
				.getInt();

		byte[] data = new byte[responseSize];

		if (responseType == PacketType.SERVERDATA_RESPONSE_VALUE.value()) {
			if (responseId == packet.getId()) {
				for (int i = 0; i < responseSize - 11; i++) {
					data[i] = buffer[12 + i];
				}
			}
		}
		System.out.println(new String(data));
		socket.close();
	}
}
