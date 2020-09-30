package com.terminal;

/**
 * Sends a response from the terminal emulator.
 *
 */
public interface TerminalOutputStream {
    void sendBytes(byte[] response);

    void sendString(final String string);
}
