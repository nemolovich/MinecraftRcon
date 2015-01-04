package fr.nemolovich.apps.minecraftrcon.gui.exceptions;

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
