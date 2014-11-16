package fr.nemolovich.apps.minecraftrcon.packet;

public enum PacketType {
	SERVERDATA_AUTH(3),
	SERVERDATA_AUTH_RESPONSE(2),
	SERVERDATA_EXECCOMMAND(2),
	SERVERDATA_RESPONSE_VALUE(0);
	
	private final int code;
	
	PacketType(int code) {
		this.code=code;
	}
	
	public int value() {
		return this.code;
	}
}
