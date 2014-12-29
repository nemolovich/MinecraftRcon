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
public class AuthenticationException extends Exception{

    /**
	 * UID.
	 */
	private static final long serialVersionUID = 1342127052187225334L;

	public AuthenticationException() {
        super("The authentication failed");
    }
}
