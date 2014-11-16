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
public class PacketBuildingException extends Exception {

    public PacketBuildingException(Exception ex) {
        super(String.format("The exception building failed: %s",
            ex.getMessage()));
        this.setStackTrace(ex.getStackTrace());
    }

}
