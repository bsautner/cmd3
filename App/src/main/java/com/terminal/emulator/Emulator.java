package com.terminal.emulator;

import java.io.IOException;


public interface Emulator {
    boolean hasNext();

    void next() throws IOException;

    void resetEof();
}
