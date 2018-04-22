/**
 * 
 */
package tes.iva.demo.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for application exceptions.
 * 
 * @author Vizlja
 */
@ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {

	private static final long serialVersionUID = -2064394694214288485L;

	/**
	 * Constructor.
	 * 
	 * @param exception
	 */
	public AppException(String exception) {

		super(exception);
	}
}
