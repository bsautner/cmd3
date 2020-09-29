package com.terminal;

import com.terminal.ui.AbstractTabbedTerminalWidget;
import com.terminal.ui.AbstractTabs;
import com.terminal.ui.JediTermWidget;
import com.terminal.ui.TerminalTabsImpl;
import com.terminal.ui.settings.TabbedSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public class TabbedTerminalWidget extends AbstractTabbedTerminalWidget<JediTermWidget> {
    public TabbedTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, @NotNull Function<AbstractTabbedTerminalWidget, JediTermWidget> createNewSessionAction) {
        super(settingsProvider, createNewSessionAction::apply);
    }

    @Override
    public JediTermWidget createInnerTerminalWidget() {
        return new JediTermWidget(getSettingsProvider());
    }

    @Override
    protected AbstractTabs<JediTermWidget> createTabbedPane() {
        return new TerminalTabsImpl();
    }
}
