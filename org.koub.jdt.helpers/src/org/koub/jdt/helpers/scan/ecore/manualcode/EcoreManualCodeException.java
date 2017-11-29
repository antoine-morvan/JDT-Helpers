package org.koub.jdt.helpers.scan.ecore.manualcode;

import org.eclipse.jdt.core.JavaModelException;

public class EcoreManualCodeException extends RuntimeException {

	public EcoreManualCodeException(final String message, final JavaModelException cause) {
		super(message,cause);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
