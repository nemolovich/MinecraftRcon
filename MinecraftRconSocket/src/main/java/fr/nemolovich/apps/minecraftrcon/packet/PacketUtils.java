package fr.nemolovich.apps.minecraftrcon.packet;

import fr.nemolovich.apps.minecraftrcon.exceptions.PacketBuildingException;
import fr.nemolovich.apps.minecraftrcon.exceptions.UnknownPacketTypeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Class providing some useful method to work with packets.
 *
 * @author Nemolovich
 */
public class PacketUtils {

    private static int PACKET_ID = 0;

    /**
     * Return the next increment ID.
     *
     * @return {@link Integer int} - The next ID.
     */
    public static final int getNextId() {
        return ++PACKET_ID;
    }

    /**
     * Insert a bytes array that represents and integer in a existing bytes
     * array at given index.
     *
     * @param target {@link Byte byte}[] - The bytes arrays to insert integer.
     * @param intValue {@link Integer integer} - The intgeger value to insert.
     * @param index {@link Integer integer} - The index to insert bytes blocks.
     */
    public static final void insertInt(byte[] target, int intValue, int index) {

        byte[] integerBytesBlock = ByteBuffer.allocate(
            PacketConstants.INTEGER_BYTES_ARRAY_BLOCK_SIZE).order(
                ByteOrder.LITTLE_ENDIAN).putInt(intValue).array();

        System.arraycopy(integerBytesBlock, 0, target, index, 4);
    }

    /**
     * Insert a bytes data array in a existing bytes array at given index.
     *
     * @param target {@link Byte byte}[] - The bytes arrays to insert integer.
     * @param data {@link Byte byte}[] - The data bytes to insert.
     * @param index {@link Integer integer} - The index to insert bytes blocks.
     */
    public static final void insertData(byte[] target, byte[] data, int index) {
        System.arraycopy(data, 0, target, index, data.length);
    }

    /**
     * Construct a packet from a bytes array got from server response.
     *
     * @param buffer {@link Byte byte}[] - The bytes array containing the
     * response from server.
     * @return {@link Packet} - The packet built from the bytes array.
     * @throws PacketBuildingException If an exception occured during the packet
     * creation.
     */
    public static final Packet bluidPacket(byte[] buffer) throws PacketBuildingException {
        Packet result = null;

        try {
            byte[] size = Arrays.copyOfRange(buffer,
                PacketConstants.SIZE_BLOCKS_INDEX,
                PacketConstants.SIZE_BLOCKS_INDEX
                + PacketConstants.SIZE_BLOCKS_SIZE);
            byte[] id = Arrays.copyOfRange(buffer,
                PacketConstants.ID_BLOCKS_INDEX,
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
            byte[] data = new byte[responseSize];
            for (int i = 0; i < responseSize; i++) {
                data[i] = buffer[PacketConstants.DATA_BLOCKS_INDEX + i];
            }

            PacketType packetType = null;

            switch (responseType) {
                case PacketConstants.SERVERDATA_AUTH_RESPONSE_CODE:
                    packetType = PacketType.SERVERDATA_AUTH_RESPONSE;
                    break;
                case PacketConstants.SERVERDATA_RESPONSE_VALUE_CODE:
                    packetType = PacketType.SERVERDATA_AUTH_RESPONSE;
                    break;
                default:
                    break;
            }
            if (packetType != null) {
                result = new Packet(responseId, packetType, new String(data));
            } else {
                throw new UnknownPacketTypeException(responseType);
            }
        } catch (Exception ex) {
            throw new PacketBuildingException(ex);
        }

        return result;
    }
}
