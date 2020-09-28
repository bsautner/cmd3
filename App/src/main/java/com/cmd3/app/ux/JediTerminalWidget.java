package com.cmd3.app.ux;

import com.cmd3.app.TerminalPanel;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.RegionPainter;
import com.terminal.SubstringFinder;
import com.terminal.TerminalStarter;
import com.terminal.TtyBasedArrayDataStream;
import com.terminal.TtyConnector;
import com.terminal.model.JediTerminal;
import com.terminal.model.StyleState;
import com.terminal.model.TerminalTextBuffer;
import com.terminal.ui.JediTermWidget;
import com.terminal.ui.settings.SettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class JediTerminalWidget extends JediTermWidget implements Disposable {

    public JediTerminalWidget(SettingsProvider settingsProvider, Disposable parent) {
        super(settingsProvider);
        setName("com/terminal");

        Disposer.register(parent, this);
    }

    @Override
    protected TerminalPanel createTerminalPanel(@NotNull SettingsProvider settingsProvider,
                                                @NotNull StyleState styleState,
                                                @NotNull TerminalTextBuffer textBuffer) {
        TerminalPanel panel = new TerminalPanel(settingsProvider, styleState, textBuffer);
        Disposer.register(this, panel);
        return panel;
    }


    @Override
    protected TerminalStarter createTerminalStarter(JediTerminal terminal, TtyConnector connector) {
        return new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector));
    }

    @Override
    protected JScrollBar createScrollBar() {
        JBScrollBar bar = new JBScrollBar();
        bar.putClientProperty(JBScrollPane.Alignment.class, JBScrollPane.Alignment.RIGHT);
        bar.putClientProperty(JBScrollBar.TRACK, (RegionPainter<Object>) (g, x, y, width, height, object) -> {
            SubstringFinder.FindResult result = myTerminalPanel.getFindResult();
            if (result != null) {
                int modelHeight = bar.getModel().getMaximum() - bar.getModel().getMinimum();
                int anchorHeight = Math.max(2, height / modelHeight);

                Color color = mySettingsProvider.getTerminalColorPalette()
                        .getColor(mySettingsProvider.getFoundPatternColor().getBackground());
                g.setColor(color);
                for (SubstringFinder.FindResult.FindItem r : result.getItems()) {
                    int where = height * r.getStart().y / modelHeight;
                    g.fillRect(x, y + where, width, anchorHeight);
                }
            }
        });
        return bar;
    }

    @Override
    public void dispose() {
    }
}