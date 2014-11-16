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

    public UnknownPacketTypeException(int responseType) {
        super(String.format("Unkown packet type '%d'", responseType));
    }

}
