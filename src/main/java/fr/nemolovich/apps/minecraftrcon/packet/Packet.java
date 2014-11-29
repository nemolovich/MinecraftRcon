package fr.nemolovich.apps.minecraftrcon.packet;

import java.io.UnsupportedEncodingException;

/**
 * The packet class defines the packet sent/received by the Minecraft server on
 * RCON protocol.
 *
 * @author Nemolovich
 */
public class Packet {

    private int id;
    private int cmd;
    private byte data[] = new byte[PacketConstants.MAX_DATA_SIZE];

    /**
     * Build the packet with specific ID.
     *
     * @param id {@link Integer int} - The packet ID.
     */
    public Packet(int id) {
        this.id = id;
    }

    /**
     * Build a packet.
     */
    public Packet() {
        this(PacketUtils.getNextId());
        this.cmd = 0;
    }

    /**
     * Build a packet with ID, type and message to send.
     *
     * @param id {@link Integer int} - The packet ID.
     * @param type {@link PacketType} - The type of the packet.
     * @param msg {@link String} - The message to send.
     */
    public Packet(int id, PacketType type, String msg) {
        this(id);
        this.cmd = type.value();
        try {
            this.data = msg.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("ENCODING ERROR");
        }
    }

    public Packet(PacketType type, String msg) {
        this(PacketUtils.getNextId(), type, msg);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getBytes() {
        // The length is the length of data, that means ID, type, data and 
        // end character (equals null char: \0)
        int length = this.data.length
            + PacketConstants.TYPE_BLOCKS_SIZE
            + PacketConstants.ID_BLOCKS_SIZE
            + PacketConstants.NULL_BLOCKS_SIZE;

        // The total bytes array take the data length plus the blocks size to
        // store the size of data
        byte[] result = new byte[length + PacketConstants.SIZE_BLOCKS_SIZE];

        PacketUtils.insertInt(result, length,
            PacketConstants.SIZE_BLOCKS_INDEX);
        PacketUtils.insertInt(result, this.id,
            PacketConstants.ID_BLOCKS_INDEX);
        PacketUtils.insertInt(result, this.cmd,
            PacketConstants.TYPE_BLOCKS_INDEX);
        PacketUtils.insertData(result, this.data,
            PacketConstants.DATA_BLOCKS_INDEX);

        return result;
    }

    public int getId() {
        return this.id;
    }

}
