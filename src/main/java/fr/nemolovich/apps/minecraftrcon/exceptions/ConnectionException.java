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
public class ConnectionException extends Exception {

    public ConnectionException(String host, int port, Exception ex) {
        super(String.format(
            "Can not access to host '%s' on port '%d'", host, port), ex);
    }

}