package com.terminal.ui.settings;

import com.terminal.TtyConnector;
import com.terminal.ui.TerminalActionPresentation;
import org.jetbrains.annotations.NotNull;


public interface TabbedSettingsProvider extends SettingsProvider {
    boolean shouldCloseTabOnLogout(TtyConnector ttyConnector);

    String tabName(TtyConnector ttyConnector, String sessionName);

    @NotNull TerminalActionPresentation getPreviousTabActionPresentation();

    @NotNull TerminalActionPresentation getNextTabActionPresentation();
}
