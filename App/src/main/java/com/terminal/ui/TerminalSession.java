package com.terminal.ui;

import com.terminal.Terminal;
import com.terminal.TtyConnector;
import com.terminal.model.TerminalTextBuffer;


public interface TerminalSession {
    void start();

    TerminalTextBuffer getTerminalTextBuffer();

    Terminal getTerminal();

    TtyConnector getTtyConnector();

    String getSessionName();

    void close();
}
