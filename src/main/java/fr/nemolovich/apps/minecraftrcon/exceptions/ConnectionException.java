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

    /**
	 * UID.
	 */
	private static final long serialVersionUID = -4630594336218123188L;
	public ConnectionException(String host, int port, Exception ex) {
        super(String.format(
            "Can not access to host '%s' on port '%d'", host, port), ex);
    }
    public ConnectionException(String message) {
        super("The connection has been cancelled");
    }

}
