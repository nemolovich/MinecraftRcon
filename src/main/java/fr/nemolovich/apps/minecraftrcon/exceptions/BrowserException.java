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
public class BrowserException extends Exception {

    /**
	 * UID.
	 */
	private static final long serialVersionUID = 8532426877520954463L;

	public BrowserException() {
        super("Can not locate default browser");
    }
    
}
