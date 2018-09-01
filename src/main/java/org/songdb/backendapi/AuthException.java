package org.songdb.backendapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthException extends Exception {

	public AuthException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -6879878862406762970L;

}
