package com.bitcoinminers.messageapp.exceptions;

/**
 * Exception to be thrown in the case that a command is callable but
 * is not yet implemented.
 * @author Christian Albina
 */
public class UnimplementedCommandException extends RuntimeException {
    public UnimplementedCommandException(String command) {
        super(String.format("Command '%s' is unimplemented", command));
    }
}
