package fr.nemolovich.apps.minecraftrcon.packet;

import java.nio.ByteBuffer;

public class PacketUtils {

	private static int PACKET_ID = 0;

	public static final int getNextId() {
		return ++PACKET_ID;
	}

	public static final void insertInt(byte[] target, int val, int index) {

		byte[] convert = ByteBuffer.allocate(4)
				.order(java.nio.ByteOrder.LITTLE_ENDIAN).putInt(val).array();
		// byte[] convert = ByteBuffer
		// .allocate(4)
		// .order(java.nio.ByteOrder.LITTLE_ENDIAN)
		// .putInt(Byte.parseByte(String.format("%02X",
		// Integer.valueOf(val)))).array();

		for (int i = 0; i < 4; i++) {
			target[i + index] = convert[i];
		}
	}

	public static final void insertData(byte[] target, byte[] val, int index) {

		for (int i = 0; i < val.length; i++) {
			target[i + index] = val[i];
		}
	}
}
