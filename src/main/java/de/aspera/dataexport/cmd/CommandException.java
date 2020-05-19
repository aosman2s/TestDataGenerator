
package de.aspera.dataexport.cmd;

/**
 *
 * @author daniel
 */
public class CommandException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8730754063101789715L;

	public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
