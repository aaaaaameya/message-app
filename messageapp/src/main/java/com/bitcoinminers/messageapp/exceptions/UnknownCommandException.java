package com.bitcoinminers.messageapp.exceptions;

/**
 * Exception to be thrown in the case that a command is unrecognised.
 * @author Christian Albina
 */
public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException(String command) {
        super(String.format("Command '%s' is unknown", command));
    }
}
