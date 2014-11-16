/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.packet;

/**
 *
 * @author Nemolovich
 */
public interface PacketConstants {

    /*
     * Minecraft server codes
     */
    public static final int SERVERDATA_AUTH_CODE = 3;
    public static final int SERVERDATA_AUTH_RESPONSE_CODE = 2;
    public static final int SERVERDATA_EXECCOMMAND_CODE = 2;
    public static final int SERVERDATA_RESPONSE_VALUE_CODE = 0;

    /*
     * 32 bit bytes array default block sizes
     */
    public static final int INTEGER_BYTES_ARRAY_BLOCK_SIZE = 4;
    public static final int CHARACTER_BYTES_ARRAY_BLOCK_SIZE = 2;

    /*
     * BUFFERS SIZES
     */
    public static final int SIZE_BLOCKS_SIZE = INTEGER_BYTES_ARRAY_BLOCK_SIZE;
    public static final int ID_BLOCKS_SIZE = INTEGER_BYTES_ARRAY_BLOCK_SIZE;
    public static final int TYPE_BLOCKS_SIZE = INTEGER_BYTES_ARRAY_BLOCK_SIZE;
    public static final int NULL_BLOCKS_SIZE = CHARACTER_BYTES_ARRAY_BLOCK_SIZE;

    public static final int MAX_DATA_SIZE = 2048;
    public static final int MAX_BUFFER_SIZE = MAX_DATA_SIZE + SIZE_BLOCKS_SIZE
        + ID_BLOCKS_SIZE + TYPE_BLOCKS_SIZE + NULL_BLOCKS_SIZE;

    /*
     * BUFFER INDEXES
     */
    public static final int SIZE_BLOCKS_INDEX = 0;
    public static final int ID_BLOCKS_INDEX = SIZE_BLOCKS_INDEX
        + SIZE_BLOCKS_SIZE;
    public static final int TYPE_BLOCKS_INDEX = ID_BLOCKS_INDEX
        + ID_BLOCKS_SIZE;
    public static final int DATA_BLOCKS_INDEX = TYPE_BLOCKS_INDEX
        + TYPE_BLOCKS_SIZE;

}
