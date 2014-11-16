package fr.nemolovich.apps.minecraftrcon.packet;

public enum PacketType {

    SERVERDATA_AUTH(PacketConstants.SERVERDATA_AUTH_CODE),
    SERVERDATA_AUTH_RESPONSE(PacketConstants.SERVERDATA_AUTH_RESPONSE_CODE),
    SERVERDATA_EXECCOMMAND(PacketConstants.SERVERDATA_EXECCOMMAND_CODE),
    SERVERDATA_RESPONSE_VALUE(PacketConstants.SERVERDATA_RESPONSE_VALUE_CODE);

    private final int code;

    PacketType(int code) {
        this.code = code;
    }

    public final int value() {
        return this.code;
    }
}
