package fr.nemolovich.apps.minecraftrcon.packet;

import java.io.UnsupportedEncodingException;


public class Packet {
	private int id;
	private int cmd;
	private byte data[] = new byte[1024];

	public Packet() {
		this.id=PacketUtils.getNextId();
		this.cmd = 0;
	}

	public Packet(PacketType type, String msg) {
		this();
		this.cmd = type.value();
		try {
			this.data = msg.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			System.err.println("ENCODING ERROR");
		}
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getBytes() {
		int length=this.data.length+10;
		
		byte[] result=new byte[length+4];

		PacketUtils.insertInt(result, length, 0);
		PacketUtils.insertInt(result, this.id, 4);
		PacketUtils.insertInt(result, this.cmd, 8);
		PacketUtils.insertData(result, this.data, 12);
		
		return result;
	}

	public int getId() {
		return this.id;
	}
	
}