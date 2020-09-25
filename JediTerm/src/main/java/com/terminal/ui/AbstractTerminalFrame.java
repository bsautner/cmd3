package com.terminal.ui;


import com.app.data.Command;
import com.terminal.TabbedTerminalWidget;
import com.terminal.TtyConnector;
import com.terminal.ui.settings.DefaultTabbedSettingsProvider;
import com.terminal.ui.settings.TabbedSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public abstract class AbstractTerminalFrame {

    private final TerminalWidget terminal;

    public TerminalWidget getTerminal() {
        return terminal;
    }


    @Nullable
    protected JediTermWidget openSession(TerminalWidget terminal) {
        if (terminal.canOpenSession()) {
            return openSession(terminal, createTtyConnector());
        }
        return null;
    }

    public JediTermWidget openSession(TerminalWidget terminal, TtyConnector ttyConnector) {
        JediTermWidget session = terminal.createTerminalSession(ttyConnector);
        session.start();
        return session;
    }

    public abstract TtyConnector createTtyConnector();

    protected AbstractTerminalFrame() {
        this.terminal = createTabbedTerminalWidget();
        openSession(terminal);
    }

    @NotNull
    protected AbstractTabbedTerminalWidget createTabbedTerminalWidget() {
        return new TabbedTerminalWidget(new DefaultTabbedSettingsProvider(), this::openSession) {
            @Override
            public JediTermWidget createInnerTerminalWidget() {
                return createTerminalWidget(getSettingsProvider());
            }
        };
    }

    protected JediTermWidget createTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider) {
        return new JediTermWidget(settingsProvider);
    }


    public void sendCommand(@NotNull Command command, boolean autoCr)   {

        try {
            terminal.getCurrentSession().getTtyConnector().write(command.getCmd());
            if (autoCr) {
                terminal.getCurrentSession().getTtyConnector().write("\n");
            }
            terminal.grabFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
