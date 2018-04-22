/**
 * 
 */
package tes.iva.demo.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for invalid user requests.
 * 
 * @author Vizlja
 */
@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1889203187995987019L;

	/**
	 * Constructor.
	 * 
	 * @param exception
	 */
	public BadRequestException(String exception) {

		super(exception);
	}
}
