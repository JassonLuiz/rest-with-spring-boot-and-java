package io.github.jassonluiz.restwithspringbootandjava.exceptions;

import java.io.Serializable;
import java.util.Date;

public class ExceptionResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date timesTamp;
	private String message;
	private String details;

	public ExceptionResponse(Date timesTamp, String message, String details) {
		this.timesTamp = timesTamp;
		this.message = message;
		this.details = details;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getTimesTamp() {
		return timesTamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

}
