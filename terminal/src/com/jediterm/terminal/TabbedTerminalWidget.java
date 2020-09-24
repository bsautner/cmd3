package com.jediterm.terminal;

import com.jediterm.terminal.command.CommandListener;
import com.jediterm.terminal.ui.AbstractTabbedTerminalWidget;
import com.jediterm.terminal.ui.AbstractTabs;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.TerminalTabsImpl;
import com.jediterm.terminal.ui.settings.TabbedSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author traff
 */
public class TabbedTerminalWidget extends AbstractTabbedTerminalWidget<JediTermWidget> {
    public TabbedTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, @NotNull Function<AbstractTabbedTerminalWidget, JediTermWidget> createNewSessionAction) {
        super(settingsProvider, createNewSessionAction::apply);
    }

    @Override
    public JediTermWidget createInnerTerminalWidget(CommandListener commandListener) {
        return new JediTermWidget(commandListener, getSettingsProvider());
    }

    @Override
    protected AbstractTabs<JediTermWidget> createTabbedPane() {
        return new TerminalTabsImpl();
    }
}
