package fr.nemolovich.apps.minecraftrcon.packet;

import java.io.UnsupportedEncodingException;

public class Packet {

    private int id;
    private int cmd;
    private byte data[] = new byte[1024];

    public Packet(int id) {
        this.id = id;
    }

    public Packet() {
        this(PacketUtils.getNextId());
        this.cmd = 0;
    }

    public Packet(int id, PacketType type, String msg) {
        this(id);
        this.cmd = type.value();
        try {
            this.data = msg.getBytes("ASCII");
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
