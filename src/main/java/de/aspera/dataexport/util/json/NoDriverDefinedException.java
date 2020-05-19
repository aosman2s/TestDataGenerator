package de.aspera.dataexport.util.json;

public class NoDriverDefinedException extends JsonConnectionReadException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7303985836274990483L;
	
	public NoDriverDefinedException (String message) {
		super(message);
	}

}
