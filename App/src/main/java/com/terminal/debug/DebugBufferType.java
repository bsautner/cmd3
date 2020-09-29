package com.terminal.debug;

import com.terminal.LoggingTtyConnector;
import com.terminal.ui.TerminalSession;


public enum DebugBufferType {
    Back() {
        public String getValue(TerminalSession session) {
            return session.getTerminalTextBuffer().getScreenLines();
        }
    },
    BackStyle() {
        public String getValue(TerminalSession session) {
            return session.getTerminalTextBuffer().getStyleLines();
        }
    },
    Scroll() {
        public String getValue(TerminalSession session) {
            return session.getTerminalTextBuffer().getHistoryBuffer().getLines();
        }
    },

    ControlSequences() {
        private ControlSequenceVisualizer myVisualizer = new ControlSequenceVisualizer();

        public String getValue(TerminalSession session) {
            if (session.getTtyConnector() instanceof LoggingTtyConnector) {
                return myVisualizer.getVisualizedString(((LoggingTtyConnector) session.getTtyConnector()).getChunks());
            } else {
                return "Control sequences aren't logged";
            }
        }
    };


    public abstract String getValue(TerminalSession session);
}
