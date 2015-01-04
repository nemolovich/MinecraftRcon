/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.exceptions;

/**
 *
 * @author Nemolovich
 */
public class UnknownPacketTypeException extends Exception {

    /**
     * UID.
     */
    private static final long serialVersionUID = 883253519726242909L;

    public UnknownPacketTypeException(int responseType) {
        super(String.format("Unkown packet type '%d'", responseType));
    }

}
