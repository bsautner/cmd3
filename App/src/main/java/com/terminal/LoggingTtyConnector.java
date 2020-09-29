package com.terminal;

import java.util.List;


public interface LoggingTtyConnector {
    List<char[]> getChunks();
}
